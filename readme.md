http://localhost:9090/saga/api-doc

curl -vv -d '{"id":"success"}' -H "Content-Type: application/json" -X POST http://localhost:9090/saga/api/do-service1

curl -vv -d '{"id":"success"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/saga4

curl -v -i -X POST -H "Content-Type:application/json" -d '{  "id" : "Test", "balance" : 0, "hold_mark": "" }' http://localhost:7070/masters                

curl -vv -d '{"account":"success", "amt":100, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx

Master
curl -vv -d '{"account":"abc", "amt":300, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx
curl -vv -d '{"account":"abc", "amt":1001, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx
curl -vv -d '{"account":"abc", "amt":1002, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx
curl -vv -d '{"account":"abc", "amt":1003, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx

Detail
curl -vv -d '{"account":"abc", "amt":2001, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx
curl -vv -d '{"account":"abc", "amt":2002, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx
curl -vv -d '{"account":"abc", "amt":2003, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx

Journal
curl -vv -d '{"account":"abc", "amt":3001, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx
curl -vv -d '{"account":"abc", "amt":3002, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx
curl -vv -d '{"account":"abc", "amt":3003, "txGuid": "1"}' -H "Content-Type: application/json" -X POST http://localhost:7070/api/tx


// saga
                .route()
                .saga()
                .timeout(2, TimeUnit.MINUTES)
                .log("############################ SAGA #${body.id} ")
                //.propagation(SagaPropagation.MANDATORY)
                .option("id", header("id"))
                .compensation("direct:compensationService1")
                .completion("direct:completeService1")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .log("############################ SAGA #${header.id} ")

                .doTry()
                .to("bean:serviceProcessor?method=doService1")
                // .process(doService1Processor)
                .doCatch(Exception.class)
                .log("############################ " + exceptionMessage().toString())
                .throwException(new RuntimeException("############################  ${header.id} failed"))
                .endDoTry()
                .log("############################ ${header.id} done")
                //--saga