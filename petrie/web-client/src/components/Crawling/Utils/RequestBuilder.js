export function buildRequestBody(
    {
        url,
        maxSearchDepth,
        scrapDynamically,
        scrapAllIfNoScenario,
        scenarios = []

    }
) {
    return {
        "url": url,
        "configuration": {
            "maxSearchDepth": parseInt(maxSearchDepth),
            "scrapAllIfNoScenario": scrapAllIfNoScenario,
            "scrapDynamically": scrapDynamically,
            "scenarios": scenarios
        }
    }
}

export function scenarioBuilder(
    {
        name = "",
        preScrapingConfigurationElementsViews = [],
        elementsToFetchUrlsFrom = [],
        urlConfiguration = [],
        topics = [],
        isRootScenario = true
    } = {}
) {
    return {
        "name": name,
        "preScrapingConfiguration": {
            "preScrapingConfigurationElementsViews": preScrapingConfigurationElementsViews
        },
        "scrapingConfiguration": {
            "elementsToFetchUrlsFrom": elementsToFetchUrlsFrom
        },
        "postScrapingConfiguration": {
            "urlConfiguration": urlConfiguration
        },
        "topicalScrapingConfiguration": {
            "topics": topics
        },
        "isRootScenario": isRootScenario
    }
}
