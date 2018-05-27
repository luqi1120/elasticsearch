package com.luqi.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luqi.entity.House;
import com.luqi.entity.HouseDetail;
import com.luqi.entity.HouseTag;
import com.luqi.repository.HouseDetailRepository;
import com.luqi.repository.HouseRepository;
import com.luqi.repository.HouseTagRepository;
import com.luqi.service.ServiceMultiResult;
import com.luqi.service.ServiceResult;
import com.luqi.web.from.MapSearch;
import com.luqi.web.from.RentSearch;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by luQi
 * 2018-05-27 14:19.
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private static final String INDEX_NAME = "xunwu";

    private static final String INDEX_TYPE = "house";

    // kafka的topic
    private static final String INDEX_TOPIC = "house_build";

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * kafka消费队列
     * @param content
     */
    @KafkaListener(topics = INDEX_TOPIC) // kafka监听的一个主题
    private void handleMessage(String content) {
        try {
            HouseIndexMessage message = objectMapper.readValue(content, HouseIndexMessage.class);

            switch (message.getOperation()) {
                case HouseIndexMessage.INDEX:
                    this.createOrUpdateIndex(message); // 对消息进行操作, 所以入参是消息而不是id
                    break;
                case HouseIndexMessage.REMOVE:
                    this.removeIndex(message);
                    break;
                default:
                    logger.warn("Not support message content " + content); // 如果都不满足就忽略消息
                    break;
            }
        } catch (IOException e) {
            logger.error("Cannot parse json for " + content, e);
        }
    }


    private void createOrUpdateIndex(HouseIndexMessage message) {

        Long houseId = message.getHouseId();

        if (houseId == null) {
            // 异常
        }
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            logger.error("Index house {} dose not exist!", houseId);

            // 进入队列
            this.index(houseId, message.getRetry() + 1);
            return;
        }
        HouseIndexTemplate indexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, indexTemplate);

        // 查询具体房屋数据
        HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
        if (detail == null) {
            // TODO 异常
        }
        modelMapper.map(detail, indexTemplate);

        // 查询tag数据
        List<HouseTag> houseTags = houseTagRepository.findAllByHouseId(houseId);
        if (houseTags != null && StringUtils.isEmpty(houseTags)) {
//            List<String> tags = houseTags.stream().map(houseTag -> houseTag.getName()).collect(Collectors.toList());
            List<String> tags = new ArrayList<>();
            houseTags.forEach(houseTag -> tags.add(houseTag.getName()));
            indexTemplate.setTags(tags);
        }

        // 查询ES中有没有索引 name + type
        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                // 查询条件
                .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));
        logger.debug(requestBuilder.toString());

        Boolean result;
        SearchResponse searchResponse = requestBuilder.get();
        long totalHit = searchResponse.getHits().getTotalHits();
        // 如果中条数为0 就创建, 为1就更新
        if (totalHit == 0) {
            result = create(indexTemplate);
        } else if (totalHit == 1) {
            String esId = searchResponse.getHits().getAt(0).getId();
            result = update(esId, indexTemplate);
        } else {
            result = deleteAndCreate(totalHit, indexTemplate);
        }
        if (result) {
            logger.debug("Index result with house " + houseId);
        }
    }

    private void removeIndex(HouseIndexMessage message) {

        Long houseId = message.getHouseId();
        // 使用DeleteByQueryAction 进行删除
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)

                // filter 传入要删除的条件
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId))

                // 索引名
                .source(INDEX_NAME);
        logger.info("Delete by query for house: {}" + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        logger.info("Delete total: {}" + deleted);

        // 如果消费失败
        if (deleted <= 0) {
            this.remove(houseId, message.getRetry() + 1);
        }
    }

    @Override
    public void index(Long houseId) {
        this.index(houseId, 0);

        // 下面是没有使用kafka直接调用
//        if (houseId == null) {
//            // 异常
//        }
//        House house = houseRepository.findOne(houseId);
//        if (house == null) {
//            logger.error("Index house {} dose not exist!", houseId);
//            return;
//        }
//        HouseIndexTemplate indexTemplate = new HouseIndexTemplate();
//        modelMapper.map(house, indexTemplate);
//
//        // 查询具体房屋数据
//        HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
//        if (detail == null) {
//            // TODO 异常
//        }
//        modelMapper.map(detail, indexTemplate);
//
//        // 查询tag数据
//        List<HouseTag> houseTags = houseTagRepository.findAllByHouseId(houseId);
//        if (houseTags != null && StringUtils.isEmpty(houseTags)) {
////            List<String> tags = houseTags.stream().map(houseTag -> houseTag.getName()).collect(Collectors.toList());
//            List<String> tags = new ArrayList<>();
//            houseTags.forEach(houseTag -> tags.add(houseTag.getName()));
//            indexTemplate.setTags(tags);
//        }
//
//        // 查询ES中有没有索引 name + type
//        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
//                // 查询条件
//                .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));
//        logger.debug(requestBuilder.toString());
//
//        Boolean result;
//        SearchResponse searchResponse = requestBuilder.get();
//        long totalHit = searchResponse.getHits().getTotalHits();
//        // 如果中条数为0 就创建, 为1就更新
//        if (totalHit == 0) {
//            result = create(indexTemplate);
//        } else if (totalHit == 1) {
//            String esId = searchResponse.getHits().getAt(0).getId();
//            result = update(esId, indexTemplate);
//        } else {
//            result = deleteAndCreate(totalHit, indexTemplate);
//        }
//        if (result) {
//            logger.debug("Index result with house " + houseId);
//        }
    }

    /**
     * 内部接口
     * @param houseId
     * @param retry
     */
    public void index(Long houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            logger.error("Retry index times over 3 for house: " + houseId + " Please check it!");
            return;
        }

        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry);
        try {

            // kafka 发送消息
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("Json encode error for " + message);
        }
    }

    // 创建
    private Boolean create(HouseIndexTemplate indexTemplate) {
        try {

            // 转换为json
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE).
                    setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

            logger.debug("Create index with house: " + indexTemplate.getHouseId());

            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Error to index house " + indexTemplate.getHouseId(), e);
            return false;
        }
    }

    // 更新基本与创建类似
    private Boolean update(String esId, HouseIndexTemplate indexTemplate) {
        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId).
                    setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
            logger.debug("Update index with house: " + indexTemplate.getHouseId());

            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Error to index house " + indexTemplate.getHouseId(), e);
            return false;
        }
    }

    /**
     * 删除和创建
     * @param totalHit  重复
     * @param indexTemplate
     * @return
     */
    private Boolean deleteAndCreate(long totalHit, HouseIndexTemplate indexTemplate) {

        // 使用DeleteByQueryAction 进行删除
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)

                // filter 传入要删除的条件
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId()))

                // 索引名
                .source(INDEX_NAME);
        logger.debug("Delete by query for house: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();

        // 查询到的和删除的数量不一致
        if (deleted != totalHit) {
            logger.warn("Need delete {}, but {} was deleted!", totalHit, deleted);
            return false;
        } else {
            return create(indexTemplate);
        }
    }

    @Override
    public void remove(Long houseId) {
        this.remove(houseId, 0);

        // 下面是没有使用kafka直接调用
        // 使用DeleteByQueryAction 进行删除
//        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
//                .newRequestBuilder(esClient)
//
//                // filter 传入要删除的条件
//                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId))
//
//                // 索引名
//                .source(INDEX_NAME);
//        logger.info("Delete by query for house: {}" + builder);
//
//        BulkByScrollResponse response = builder.get();
//        long deleted = response.getDeleted();
//        logger.info("Delete total: {}" + deleted);
    }

    private void remove(Long houseId, int retry) {

        if (retry > HouseIndexMessage.MAX_RETRY) {
            logger.error("Retry remove times over 3 for house: " + houseId + " Please check it!");
            return;
        }

        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
        try {

            // 发送消息
            this.kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("Cannot encode json for " + message, e);
        }
    }

}
