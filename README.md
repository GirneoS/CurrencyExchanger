## Организация Эндпойнтов REST API
___

**GET /CurrencyExchanger/api/currencies** - получить список доступных валют

**POST /CurrencyExchanger/api/currencies** - добавить валюту(x-www-urlencoded поля: name, code, sign)

**GET /CurrencyExchanger/api/currency/{code}** - получить валюту с определенным кодом

___

**GET /CurrencyExchanger/api/exchangeRates** - получить список валютных пар

**GET /CurrencyExchanger/api/exchangeRate/{baseCurrencyCode + targetCurrencyCode}** - получить конкретную валютную пару

**POST /CurrencyExchanger/api/exchangeRates** - добавить валютную пару(x-www-urlencoded поля: baseCurrencyCode(валюта, из которой будет совершаться обмен), targetCurrencyCode(валюта, в которую будет совершаться обмен), rate(курс, по которому будет совершаться обмен))

**PATCH /CurrencyExchanger/api/exchangeRate/{baseCurrencyCode + targetCurrencyCode}?rate=RATE** - обновить обменный курс валютной пары

___

**GET /CurrencyExchanger/api/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT** - совершить обмен
