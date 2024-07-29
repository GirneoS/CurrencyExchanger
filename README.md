## Организация Эндпойнтов REST API пользователей
___

**GET /currencies** - получить список доступных валют

**POST /currencies** - добавить валюту(x-www-urlencoded поля: name, code, sign)

**GET /currency/{code}** - получить валюту с определенным кодом

___

**GET /exchangeRates** - получить список валютных пар

**GET /exchangeRate/{baseCurrencyCode + targetCurrencyCode}** - получить конкретную валютную пару

**POST /exchangeRates** - добавить валютную пару(x-www-urlencoded поля: baseCurrencyCode(валюта, из которой будет совершаться обмен), targetCurrencyCode(валюта, в которую будет совершаться обмен), rate(курс, по которому будет совершаться обмен))

**PATCH /exchangeRate/{baseCurrencyCode + targetCurrencyCode}?rate=RATE** - обновить обменный курс валютной пары

___

**GET /exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT** - совершить обмен
