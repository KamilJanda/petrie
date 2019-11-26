import React from "react";
import {Grid, withStyles} from "@material-ui/core";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import TextField from "@material-ui/core/TextField";
import Icon from "@material-ui/core/Icon";
import Button from "@material-ui/core/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import RadioGroup from "@material-ui/core/RadioGroup";
import Radio from "@material-ui/core/Radio";
import FormLabel from "@material-ui/core/FormLabel";
import styles from "./Style/CrawlerStyle";
import {buildRequestBody, scenarioBuilder} from "./Utils/RequestBuilder";
import ScrapingScenario from "./ScrapingScenario";
import UrlPriority from "./UrlPriority";

const requestUrl = "http://localhost:9000/core/links";
const testRequestUrl = "http://localhost:9000/core/links/test";

const {Map} = require('immutable');

class SimpleCrawler extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            response: [],
            requestUrl: "",
            depth: 0,
            crawlDynamically: false,
            scrapAllIfNoScenario: true,
            scrapingScenariosView: [],
            scenarios: new Map(),
            scrapingScenariosCounter: 0,
            isTopicalCrawling: false,
            urlPrioritiesView: [],
            urlPriorities: new Map(),
            urlPrioritiesCounter: 0,
        }
    }

    sendRequest = (isTest) => {

        console.log(buildRequestBody({
            url: this.state.requestUrl,
            maxSearchDepth: this.state.depth,
            scrapDynamically: this.state.crawlDynamically,
            scrapAllIfNoScenario: this.state.scrapAllIfNoScenario,
            urlPriorities: this.state.urlPriorities.valueSeq().toArray(),
            scenarios: this.state.scenarios.valueSeq().toArray()
        }));
        const url = isTest ? testRequestUrl: requestUrl;

        fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(
                buildRequestBody({
                    url: this.state.requestUrl,
                    maxSearchDepth: this.state.depth,
                    scrapDynamically: this.state.crawlDynamically,
                    scrapAllIfNoScenario: this.state.scrapAllIfNoScenario,
                    urlPriorities: this.state.urlPriorities.valueSeq().toArray(),
                    scenarios: this.state.scenarios.valueSeq().toArray()
                })
            )

        }).then(res => res.json())
            .then(response =>
                this.setState({response: JSON.stringify(response)})
            )
            .catch(error => {
                console.error('Error:', error);
                this.setState({response: error.message})
            });
    };

    handleUrlChange = event => {
        this.setState({
            requestUrl: event.target.value
        })
    };

    handleDepthChange = event => {
        this.setState({
            depth: event.target.value
        })
    };

    handleScenarioChange = (scenarioId, value) => {
        this.setState(prevState => ({scenarios: prevState.scenarios.set(scenarioId, value)}));
    };

    handleUrlPriorityChange = (urlPriorityId, value) => {
        this.setState(prevState => ({urlPriorities: prevState.urlPriorities.set(urlPriorityId, value)}));
    };

    addScrapingScenario = () => {
        const key = this.state.scrapingScenariosCounter;

        this.setState(prevState => ({
            scrapingScenariosView: [...prevState.scrapingScenariosView,
                <ScrapingScenario
                    key={key}
                    id={key}
                    close={this.deleteScrapingScenario}
                    onChange={this.handleScenarioChange}
                    getScenariosNames={this.getScenariosNames}
                    isDynamicCrawling={this.state.crawlDynamically}
                    isTopicalCrawling={this.state.isTopicalCrawling}
                />
            ],
            scrapingScenariosCounter: key + 1
        }));
        this.handleScenarioChange(key, scenarioBuilder())
    };

    deleteScrapingScenario = (itemId) => {
        const update = this.state.scrapingScenariosView.filter(el => el.key != itemId);

        this.setState(prevState => ({scenarios: prevState.scenarios.delete(itemId)}));

        this.setState({
            scrapingScenariosView: update
        })
    };

    addUrlPriority = () => {
        const key = this.state.urlPrioritiesCounter;
        const priority = "LowPriority";

        this.setState(prevState => ({
            urlPrioritiesView: [...prevState.urlPrioritiesView,
                <UrlPriority
                    key={key}
                    id={key}
                    onChange={this.handleUrlPriorityChange}
                    close={this.deleteUrlPriority}
                    defaultPriority={priority}
                />
            ],
            urlPrioritiesCounter: key + 1
        }));
        this.handleUrlPriorityChange(key, {url: "", priority: priority})
    };

    deleteUrlPriority = (itemId) => {
        const update = this.state.urlPrioritiesView.filter(el => el.key != itemId);

        this.setState({
            urlPrioritiesView: update
        })
    };

    getScenariosNames = () => {
        return this.state.scenarios.valueSeq().map(scenario => scenario.name);
    };

    handleCrawlingTypeRadio = (e) => {
        this.setState({
            crawlDynamically: (e.currentTarget.value === "true")
        })
    };

    handleCrawlingWhenNoScenarioRadio = (e) => {
        this.setState({
            scrapAllIfNoScenario : (e.currentTarget.value === "true")
        })
    };

    handleIsTopicalCrawlingRadio = (e) => {
        this.setState(prevState => ({
            isTopicalCrawling: !prevState.isTopicalCrawling
        }))
    };

    crawType = () => {
        if (this.state.crawlDynamically) {
            return "Dynamically"
        } else {
            return "Async"
        }
    };

    render() {
        const {classes} = this.props;

        return (
            <>
                <Grid
                    container
                    direction="row"
                    justify="center"
                    alignItems="center"
                >
                    <Grid item xs={12}>
                        <Card className={classes.card}>
                            <CardContent>
                                <TextField
                                    id="url"
                                    label="Request url"
                                    fullWidth
                                    className={classes.textField}
                                    margin="normal"
                                    onChange={this.handleUrlChange}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}

                                />

                                <TextField
                                    id="depth"
                                    label="Max depth of crawling jumps via urls"
                                    className={classes.textField}
                                    margin="normal"
                                    type="number"
                                    onChange={this.handleDepthChange}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}

                                />

                                <FormLabel
                                    component="legend"
                                    className={classes.group}
                                >
                                    Crawling Type
                                </FormLabel>
                                <RadioGroup
                                    aria-label="Crawling Type"
                                    name="Crawling Type"
                                    className={classes.group}
                                    value={this.state.isTopicalCrawling.toString()}
                                    onChange={this.handleIsTopicalCrawlingRadio}
                                >
                                    <FormControlLabel value="true" control={<Radio color="primary"/>}
                                                      label="Crawl data using topics"/>
                                    <FormControlLabel value="false" control={<Radio color="primary"/>}
                                                      label="Crawl data using selectors"/>
                                </RadioGroup>

                                <FormLabel
                                    component="legend"
                                    className={classes.group}
                                >
                                    Website Type
                                </FormLabel>
                                <RadioGroup
                                    aria-label="Crawling Type"
                                    name="Crawling Type"
                                    className={classes.group}
                                    value={this.state.crawlDynamically.toString()}
                                    onChange={this.handleCrawlingTypeRadio}
                                >
                                    <FormControlLabel value="true" control={<Radio color="primary"/>}
                                                      label="Dynamic and static websites"/>
                                    <FormControlLabel value="false" control={<Radio color="primary"/>}
                                                      label="Only static websites"/>
                                </RadioGroup>

                                <FormLabel
                                    component="legend"
                                    className={classes.group}
                                >
                                    Scraping strategy without scenario
                                </FormLabel>
                                <RadioGroup
                                    aria-label="Scraping strategy without scenario"
                                    name="Scraping strategy without scenario"
                                    className={classes.group}
                                    value={this.state.scrapAllIfNoScenario.toString()}
                                    onChange={this.handleCrawlingWhenNoScenarioRadio}
                                >
                                    <FormControlLabel value="true" control={<Radio color="primary"/>}
                                                      label="Scrap all data if no scenario defined"/>
                                    <FormControlLabel value="false" control={<Radio color="primary"/>}
                                                      label="Don't scrap data if scenario not defined"/>
                                </RadioGroup>


                                <ButtonGroup
                                    variant="contained"
                                    className={classes.buttonGroup}
                                >
                                    <Button
                                        onClick={this.addUrlPriority}>
                                        Add host with its search priority
                                    </Button>
                                </ButtonGroup>
                                <div>
                                    <ol>
                                        {this.state.urlPrioritiesView}
                                    </ol>
                                </div>

                                <ButtonGroup
                                    variant="contained"
                                    className={classes.buttonGroup}>
                                    <Button
                                        onClick={this.addScrapingScenario}>
                                        Add Scenario
                                    </Button>
                                </ButtonGroup>
                            </CardContent>

                            <CardContent>

                                <div>
                                    <ol>
                                        {this.state.scrapingScenariosView}
                                    </ol>
                                </div>
                            </CardContent>

                            <div>
                                <TextField
                                    id="response"
                                    label="Response"
                                    multiline
                                    rows="6"
                                    value={this.state.response}
                                    className={classes.textField}
                                    margin="normal"
                                    variant="outlined"
                                    InputProps={{
                                        readOnly: true,
                                    }}
                                />
                            </div>

                            <Button
                                variant="contained"
                                color="primary"
                                className={classes.button}
                                onClick={() => this.sendRequest(false)}
                            >
                                Crawl data {this.crawType()}!
                                <Icon className={classes.rightIcon}>send</Icon>
                            </Button>
                            <Button
                                variant="contained"
                                color="primary"
                                className={classes.button}
                                onClick={() => this.sendRequest(true)}
                            >
                                Test crawling
                                <Icon className={classes.rightIcon}>send</Icon>
                            </Button>

                        </Card>
                    </Grid>
                </Grid>
            </>
        );
    }
}

export default withStyles(styles)(SimpleCrawler);
