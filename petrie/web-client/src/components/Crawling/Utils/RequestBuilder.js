export function buildRequestBody(
    {
        url,
        maxSearchDepth,
        scrapDynamically,
        scrapAllIfNoScenario = true,
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
        name,
        elementsToClick = [],
        elementsToFetchUrlsFrom = [],
        urlConfiguration = [],
        isRootScenario = true
    }
) {
    return {
        "name": name,
        "preScrapingConfiguration": {
            "elementsToClick": elementsToClick
        },
        "scrapingConfiguration": {
            "elementsToFetchUrlsFrom": elementsToFetchUrlsFrom
        },
        "postScrapingConfiguration": {
            "urlConfiguration": urlConfiguration
        },
        "isRootScenario": isRootScenario
    }
}
