package tw.com.firstbank.processor;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.annotation.SagaInfo;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.repository.SagaLogRepository;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.repository.model.SagaLogData;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.repository.model.SagaLogDataKey;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.type.SagaStatus;

import javax.transaction.Transactional;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * 此類別用來做為 SAGA orchestrator 的基礎類別.
 * @param <I> 輸入資料型別
 * @param <O> 輸出資料型別
 */
abstract class BaseSagaProcessor<I, O> extends RouteBuilder implements SagaProcessor<I, O> {

    public static final String SAGA_ID_NAME = "saga_id";
    public static final String SAGA_SEQ_NAME = "saga_seq";
    public static final String DEFAULT_PARENT_SAGA_DATA_NAME = "tx_saga_data";
    public static final Integer DEFAULT_SAGA_TIMEOUT_SECONDS = 8; //180;

    protected Integer SAGA_TIMEOUT_SECONDS = DEFAULT_SAGA_TIMEOUT_SECONDS;
    protected String SAGA_NAME = "";
    protected String SAGA_ROUTE_NAME = "";
    protected String SAGA_COMPENSATE_NAME = "";
    protected String SAGA_COMPLETE_NAME = "";
    protected String SAGA_DATA_NAME = "";
    protected String PARENT_SAGA_DATA_NAME = DEFAULT_PARENT_SAGA_DATA_NAME;

    @Autowired
    protected SagaLogRepository sagaLogRepository;

    @Autowired
    protected ProducerTemplate producerTemplate;

    @Autowired
    protected CamelContext camelContext;

    protected String sagaId = "";
    protected String sagaSeq = "";

    private void setConstNames(String name) {
        SAGA_NAME = name;
        SAGA_ROUTE_NAME = "direct:" + SAGA_NAME;
        SAGA_COMPENSATE_NAME = "direct:compensate_" + SAGA_NAME;
        SAGA_COMPLETE_NAME = "direct:complete_" + SAGA_NAME;
        SAGA_DATA_NAME = SAGA_NAME + "_data";
    }

    public BaseSagaProcessor() {
        super();
        SagaInfo sagaInfo = getClass().getAnnotation(SagaInfo.class);
        if (sagaInfo != null) {
            setConstNames(sagaInfo.name());
        }
    }

    public BaseSagaProcessor(String name) {
        super();
        setConstNames(name);
    }

    @Override
    public void configure() throws Exception {
        configureSAGA();
    }

    @Override
    public O doSAGA(final String routeName, final I input, final Class<O> typeOutputClass) {
        List<Route> routes = producerTemplate.getCamelContext().getRoutes();
        routes.stream().forEach(r -> log.debug("##### {}", r.toString()));

        return producerTemplate.requestBody(routeName, input, typeOutputClass);
    }

    // Predicate

    /**
     * 此功能用來封裝使用者提供的功能以串接 camel 判斷程序.
     * @param func 使用者定義的功能
     * @param args 使用者定義功能的輸入參數
     * @param <P> 輸入參數型別
     * @return camel Predicate
     */
    public static <P> Predicate predicateFactory(final Function<P, java.lang.Boolean> func, final P args) {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Boolean ret = func.apply(args);
                return ret.booleanValue();
            }

            @Override
            public void init(CamelContext context) {
                Predicate.super.init(context);
            }

            @Override
            public void initPredicate(CamelContext context) {
                Predicate.super.initPredicate(context);
            }
        };
    }

    /**
     * 此功能用來封裝使用者提供的功能以串接 camel 判斷程序.
     * @param func 使用者定義的功能
     * @param inputClazz 輸入參數的型別
     * @param <P> 輸入參數型別
     * @return camel Predicate
     */
    public static <P> Predicate predicateFactory(final Function<P, java.lang.Boolean> func, Class<P> inputClazz) {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                P args = exchange.getIn().getBody(inputClazz);
                Boolean ret = func.apply(args);
                return ret.booleanValue();
            }

            @Override
            public void init(CamelContext context) {
                Predicate.super.init(context);
            }

            @Override
            public void initPredicate(CamelContext context) {
                Predicate.super.initPredicate(context);
            }
        };
    }

    /**
     * 此功能用來封裝使用者提供的功能以串接 camel 判斷程序.
     * @param func 使用者定義的功能
     * @param key Property Key
     * @param <P> 輸入參數型別
     * @return camel Predicate
     */
    public static <P> Predicate predicateByProperty(final Function<P, java.lang.Boolean> func, final String key) {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                P args = (P) exchange.getProperties().get(key);
                Boolean ret = func.apply(args);
                return ret.booleanValue();
            }

            @Override
            public void init(CamelContext context) {
                Predicate.super.init(context);
            }

            @Override
            public void initPredicate(CamelContext context) {
                Predicate.super.initPredicate(context);
            }
        };
    }

    public static <P> Predicate predicateByHeader(final Function<P, java.lang.Boolean> func, final String key) {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                P args = (P) exchange.getIn().getHeader(key);
                Boolean ret = func.apply(args);
                return ret.booleanValue();
            }

            @Override
            public void init(CamelContext context) {
                Predicate.super.init(context);
            }

            @Override
            public void initPredicate(CamelContext context) {
                Predicate.super.initPredicate(context);
            }
        };
    }

    // Processor
    /**
     * 此功能用來封裝使用者提供的功能以串接 camel 處理程序.
     * @param func 使用者定義的功能
     * @param args 使用者定義功能的輸入參數
     * @param <P> 輸入參數型別
     * @param <R> 輸出參數型別
     * @return camel Processor
     */
    public static <P, R> Processor processorFactory(final Function<P, R> func, final P args) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                //todo: 記錄 saga 流程的開始
                R ret = func.apply(args);
                exchange.getIn().setBody(ret);
                //todo: 記錄 saga 流程的結束
            }
        };
    }

    /**
     * 此功能用來封裝使用者提供的功能以串接 camel 處理程序.
     * @param func 使用者定義的功能
     * @param inputClazz 輸入參數的型別
     * @param <P> 輸入參數型別
     * @param <R> 輸出參數型別
     * @return camel Processor
     */
    public static <P, R> Processor processorFactory(final Function<P, R> func, Class<P> inputClazz) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                //todo: 記錄 saga 流程的開始
                P args = exchange.getIn().getBody(inputClazz);
                R ret = func.apply(args);
                exchange.getIn().setBody(ret);
                //todo: 記錄 saga 流程的結束
            }
        };
    }

    public static <P, R> Processor completeProcessorFactory(final Function<P, R> func, Class<P> inputClazz) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                //todo: 記錄 saga 流程的開始
                P args = exchange.getIn().getBody(inputClazz);
                R ret = func.apply(args);
                exchange.getIn().setBody(ret);
                //todo: 記錄 saga 流程的結束
                //todo: 記錄整個 saga 流程的結束
            }
        };
    }

    public static <P, R> Processor compensateProcessorFactory(final Function<P, R> func, Class<P> inputClazz) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                //todo: 記錄 saga 流程的開始
                P args = exchange.getIn().getBody(inputClazz);
                R ret = func.apply(args);
                exchange.getIn().setBody(ret);
                //todo: 記錄 saga 流程的結束
                //todo: 記錄整個 saga 流程的結束
            }
        };
    }

    public String generateSagaSeq() {
        this.sagaSeq = UUID.randomUUID().toString();
        return this.sagaSeq;
    }

    public String generateSagaId() {
        this.sagaId = UUID.randomUUID().toString();
        return this.sagaId;
    }

    public String getSagaId() {
        return this.sagaId;
    }

    public String getSagaSeq() {
        return this.sagaSeq;
    }

    public Expression getId() {
        return new Expression() {
            @Override
            public <String> String evaluate(Exchange exchange, Class<String> type) {
                return (String) getSagaId();
            }
        };
    }

    public Expression setId(String id) {
        this.sagaId = id;  //設定為 singleton 物件的屬性 是否會有問題?
        return new Expression() {
            @Override
            public <String> String evaluate(Exchange exchange, Class<String> type) {
                return (String) getSagaId();
            }
        };
    }

    public <P> Expression setIdByInputMethodName(String name, Class<P> inputClazz) {
        return new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                P args = exchange.getIn().getBody(inputClazz);
                try {
                    Method method = inputClazz.getDeclaredMethod(name);
                    String id = (String) method.invoke(args);
                    setId(id);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return (T) getSagaId();
            }
        };
    }

    public Expression getSeq() {
        return new Expression() {
            @Override
            public <String> String evaluate(Exchange exchange, Class<String> type) {
                return (String) getSagaSeq();
            }
        };
    }

    public Expression setSeq(String seq) {
        this.sagaSeq = seq;
        return new Expression() {
            @Override
            public <String> String evaluate(Exchange exchange, Class<String> type) {
                return (String) getSagaSeq();
            }
        };
    }

    public Expression generateId() {
        return new Expression() {
            @Override
            public <String> String evaluate(Exchange exchange, Class<String> type) {
                return (String) generateSagaId();
            }
        };
    }

    public Expression generateSeq() {
        return new Expression() {
            @Override
            public <String> String evaluate(Exchange exchange, Class<String> type) {
                return (String) generateSagaSeq();
            }
        };
    }

    protected Boolean startProcess() {
        return true;
    }

    protected Boolean compensationProcess() {
        return true;
    }

    protected Boolean completeProcess() {
        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    private void compareAndSet(String sagaId, String sagaSeq, SagaStatus status) {
        SagaLogDataKey key = new SagaLogDataKey(sagaId, sagaSeq);
        Optional<SagaLogData> opt = sagaLogRepository.findById(key);
        SagaLogData sagaLogData = null;
        if (opt.isPresent()) {
            sagaLogData = opt.get();
        } else {
            sagaLogData = new SagaLogData(key);
            sagaLogData.setStatus(SagaStatus.NONE);
        }

        log.debug("compareAndSet {} {} {} -> {}", sagaId, sagaSeq, sagaLogData.getStatus(), status);

        if (sagaLogData.getStatus() == SagaStatus.NONE && status == SagaStatus.RUNNING) {
            sagaLogData.setStatus(status);
            sagaLogRepository.save(sagaLogData);
            sagaLogRepository.flush();
            return;
        }

        if (sagaLogData.getStatus() == SagaStatus.RUNNING && status == SagaStatus.DONE) {
            sagaLogData.setStatus(status);
            sagaLogRepository.save(sagaLogData);
            sagaLogRepository.flush();
            return;
        }

        //
        if (sagaLogData.getStatus() == SagaStatus.RUNNING && status == SagaStatus.COMPENSATING) {
            sagaLogData.setStatus(status);
            sagaLogRepository.save(sagaLogData);
            sagaLogRepository.flush();
            return;
        }
        if (sagaLogData.getStatus() == SagaStatus.RUNNING && status == SagaStatus.COMPENSATED) {
            sagaLogData.setStatus(status);
            sagaLogRepository.save(sagaLogData);
            sagaLogRepository.flush();
            return;
        }
        // --

        if (sagaLogData.getStatus() == SagaStatus.DONE &&
                (status == SagaStatus.COMPENSATING || status == SagaStatus.COMPLETING)) {
            sagaLogData.setStatus(status);
            sagaLogRepository.save(sagaLogData);
            sagaLogRepository.flush();
            return;
        }

        if (sagaLogData.getStatus() == SagaStatus.COMPLETING && status == SagaStatus.COMPLETED) {
            sagaLogData.setStatus(status);
            sagaLogRepository.save(sagaLogData);
            sagaLogRepository.flush();
            return;
        }

        if (sagaLogData.getStatus() == SagaStatus.COMPENSATING && status == SagaStatus.COMPENSATED) {
            sagaLogData.setStatus(status);
            sagaLogRepository.save(sagaLogData);
            sagaLogRepository.flush();
            return;
        }

        throw new IllegalStateException("Invalid SAGA status change " + sagaId + " " + sagaSeq + " " + sagaLogData.getStatus() + " " + status);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    private void setSagaStatus(String sagaId, String sagaSeq, SagaStatus status) {
        SagaLogDataKey key = new SagaLogDataKey(sagaId, sagaSeq);
        Optional<SagaLogData> opt = sagaLogRepository.findById(key);
        SagaLogData sagaLogData = null;
        if (opt.isPresent()) {
            sagaLogData = opt.get();
        } else {
            sagaLogData = new SagaLogData(key);
            sagaLogData.setStatus(SagaStatus.NONE);
        }
        sagaLogData.setStatus(status);
        sagaLogRepository.save(sagaLogData);
        sagaLogRepository.flush();
        return;
    }

    public Boolean isSagaRunnable(String sagaId, String sagaSeq) {
        // 檢核程序
        Optional<SagaLogData> opt = sagaLogRepository.findById(new SagaLogDataKey(sagaId, sagaSeq));
        if (opt.isPresent()) {
            if (opt.get().getStatus() == SagaStatus.NONE) {
                return true;
            }
        } else {
            return true;
        }

        return false;
    }

    public Boolean isSagaCompletable(String sagaId, String sagaSeq) {
        Optional<SagaLogData> opt = sagaLogRepository.findById(new SagaLogDataKey(sagaId, sagaSeq));
        if (opt.isPresent()) {
            if (opt.get().getStatus() == SagaStatus.DONE) {
                return true;
            }
        }
        return false;
    }

    public Boolean isSagaCompensatable(String sagaId, String sagaSeq) {
        Optional<SagaLogData> opt = sagaLogRepository.findById(new SagaLogDataKey(sagaId, sagaSeq));
        if (opt.isPresent()) {
            log.debug("isSagaCompensatable {}", opt.get().getStatus());
            if (opt.get().getStatus() == SagaStatus.DONE || opt.get().getStatus() == SagaStatus.RUNNING) {
                return true;
            }
            log.debug("isSagaCompensatable INVALID status {}", opt.get().getStatus());
        }
        log.debug("isSagaCompensatable SAGA LOG NOT FOUND {} {}", sagaId, sagaSeq);
        return false;
    }

    // implements Predicate interface

    /**
     * 判斷當可以執行 saga 流程時，將 saga 狀態設定為 running.
     * @return Camel Predicate
     */
    public Predicate canExecuteSaga() {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                final String sagaId = exchange.getProperty(SAGA_ID_NAME, String.class);
                final String sagaSeq = exchange.getProperty(SAGA_SEQ_NAME, String.class);
                log.debug("canExecuteSaga {} {}", sagaId, sagaSeq);
                if (isSagaRunnable(sagaId, sagaSeq)){
                    compareAndSet(sagaId, sagaSeq, SagaStatus.RUNNING);
                    return true;
                }
                return false;
            }
            @Override
            public void init(CamelContext context) {
                Predicate.super.init(context);
            }
            @Override
            public void initPredicate(CamelContext context) {
                Predicate.super.initPredicate(context);
            }
        };
    }

    /**
     * 判斷當可以執行 saga complete 流程時，將 saga 狀態設定為 completing.
     * @return Camel Predicate
     */
    public Predicate canCompleteSaga() {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                final String sagaId = exchange.getIn().getHeader(SAGA_ID_NAME, String.class);
                final String sagaSeq = exchange.getIn().getHeader(SAGA_SEQ_NAME, String.class);
                if (isSagaCompletable(sagaId, sagaSeq)){
                    compareAndSet(sagaId, sagaSeq, SagaStatus.COMPLETING);
                    return true;
                }
                return false;
            }
            @Override
            public void init(CamelContext context) {
                Predicate.super.init(context);
            }
            @Override
            public void initPredicate(CamelContext context) {
                Predicate.super.initPredicate(context);
            }
        };
    }

    /**
     * 判斷當可以執行 saga compensate 流程時，將 saga 狀態設定為 compensating.
     * @return Camel Predicate
     */
    public Predicate canCompensateSaga() {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                final String sagaId = exchange.getIn().getHeader(SAGA_ID_NAME, String.class);
                final String sagaSeq = exchange.getIn().getHeader(SAGA_SEQ_NAME, String.class);
                if (isSagaCompensatable(sagaId, sagaSeq)){
                    compareAndSet(sagaId, sagaSeq, SagaStatus.COMPENSATING);
                    return true;
                }
                return false;
            }
            @Override
            public void init(CamelContext context) {
                Predicate.super.init(context);
            }
            @Override
            public void initPredicate(CamelContext context) {
                Predicate.super.initPredicate(context);
            }
        };
    }

    public Object sagaDone(Exchange exchange) {
        String id = exchange.getProperty(SAGA_ID_NAME, String.class);
        String seq = exchange.getProperty(SAGA_SEQ_NAME, String.class);
        Optional<SagaLogData> opt = sagaLogRepository.findById(new SagaLogDataKey(id, seq));
        log.debug("Saga Done {} {}", id, seq);
        if (opt.isPresent()) {
            log.debug("Saga Done {}", opt.get().getStatus());
            compareAndSet(id, seq, SagaStatus.DONE);
        } else {
            log.debug("Saga Done NONE", id, seq);
        }
        return exchange.getIn().getBody();
    }

    /**
     * 於 complete 階段必需由 header 取得資料
     * @param exchange Camel Exchange
     * @return camel body
     */
    public Object sagaCompleted(Exchange exchange) {
        log.debug("Saga Complete {}", exchange.toString());
        String id = exchange.getIn().getHeader(SAGA_ID_NAME, String.class);
        String seq = exchange.getIn().getHeader(SAGA_SEQ_NAME, String.class);
        Optional<SagaLogData> opt = sagaLogRepository.findById(new SagaLogDataKey(id, seq));
        if (opt.isPresent()) {
            compareAndSet(id, seq, SagaStatus.COMPLETED);
        }
        return exchange.getIn().getBody();
    }

    /**
     * 於 compensation 階段必需由 header 取得資料
     * @param exchange Camel Exchange
     * @return camel body
     */
    public Object sagaCompensated(Exchange exchange) {
        log.debug("Saga Compensated {}", exchange.toString());
        String id = exchange.getIn().getHeader(SAGA_ID_NAME, String.class);
        String seq = exchange.getIn().getHeader(SAGA_SEQ_NAME, String.class);
        Optional<SagaLogData> opt = sagaLogRepository.findById(new SagaLogDataKey(id, seq));
        if (opt.isPresent()) {
            compareAndSet(id, seq, SagaStatus.COMPENSATED);
        }
        return exchange.getIn().getBody();
    }

    abstract public void configureSAGA() throws Exception;

}
