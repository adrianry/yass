/// <reference path="contract"/>

module tutorial {

    export function log(...args: any[]): void {
        console.log.apply(console, args);
    }

    function logger(type: string): yass.Interceptor {
        return (style, method, parameters, proceed) => {
            function doLog(kind: string, data: any): void {
                log("logger:", type, yass.SESSION ? (<Session>yass.SESSION).createTime : null, kind, yass.InvokeStyle[style], method, data);
            }
            doLog("entry", parameters);
            try {
                var result = proceed();
                doLog("exit", result);
                return result;
            } catch (e) {
                doLog("exception", e);
                throw e;
            }
        };
    }
    var clientLogger = logger("client");
    var serverLogger = logger("server");

    class PriceListenerImpl implements contract.PriceListener {
        newPrices(prices: contract.Price[]): void {
            log("newPrices:", prices);
        }
    }

    class EchoServiceImpl implements contract.EchoService {
        echo(value: any): any {
            return value;
        }
    }

    function subscribePrices(invokerFactory: yass.InvokerFactory): void {
        var instrumentServiceInvoker = invokerFactory.invoker(contract.ServerServices.InstrumentService);
        var priceEngineInvoker = invokerFactory.invoker(contract.ServerServices.PriceEngine);
        // create proxies; you can add 0..n interceptors to a proxy
        var instrumentService = instrumentServiceInvoker(clientLogger);
        var priceEngine = priceEngineInvoker(clientLogger);
        instrumentService.reload(true, 987654); // oneway method call
        instrumentService.getInstruments().then(
            instruments => priceEngine.subscribe(instruments.map(instrument => instrument.id))
        ).then(
            () => log("subscribe succeeded")
        );
        priceEngine.subscribe(["unknownId"]).catch(exception => log("subscribe failed with", exception));
    }

    class Session implements yass.Session {
        createTime = Date.now();
        constructor(private sessionInvokerFactory: yass.SessionInvokerFactory) {
            // empty
        }
        opened(): void {
            log("session opened", this.createTime);
            subscribePrices(this.sessionInvokerFactory);
            setTimeout(() => this.sessionInvokerFactory.close(), 5000);
        }
        closed(exception: any): void {
            log("session closed", this.createTime, exception);
        }
    }

    yass.connect(
        "ws://localhost:9090/tutorial",
        contract.SERIALIZER,
        yass.server( // you can add 0..n interceptors to a service
            new yass.Service(contract.ClientServices.PriceListener, new PriceListenerImpl, serverLogger),
            new yass.Service(contract.ClientServices.EchoService, new EchoServiceImpl, serverLogger)
        ),
        sessionInvokerFactory => new Session(sessionInvokerFactory),
        () => log("connect failed")
    );

    // shows xhr usage
    class XhrClient extends yass.Client {
        constructor(url: string, serializer: yass.Serializer) {
            super(function (invocation: yass.ClientInvocation) {
                return invocation.invoke(yass.DIRECT, (request, rpc) => {
                    var xhr = new XMLHttpRequest();
                    xhr.responseType = "arraybuffer";
                    xhr.onerror = () => rpc.settle(new yass.ExceptionReply(new Error(xhr.statusText)));
                    xhr.onload = () => {
                        try {
                            rpc.settle(yass.readFrom(serializer, xhr.response));
                        } catch (e) {
                            rpc.settle(new yass.ExceptionReply(e));
                        }
                    };
                    xhr.open("POST", url);
                    xhr.send(yass.writeTo(serializer, request));
                });
            });
            serializer = new yass.MessageSerializer(serializer);
        }
    }
    var invokerFactory = new XhrClient("http://localhost:9090/xhr", contract.SERIALIZER);
    var echoService = invokerFactory.invoker(contract.ServerServices.EchoService)(clientLogger);
    echoService.echo("Hello World!").then(
        result => log("echo succeeded:", result),
        error => log("echo failed:", error)
    );

}
