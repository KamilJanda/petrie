import React, {Component} from 'react';
import {Grid, withStyles} from "@material-ui/core";
import TextField from "@material-ui/core/TextField";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Button from "@material-ui/core/Button";
import Icon from "@material-ui/core/Icon";
import DeleteIcon from '@material-ui/icons/Delete';
import ButtonGroup from "@material-ui/core/ButtonGroup";
import {buildRequestBody, scenarioBuilder} from "./Utils/RequestBuilder";
import FormLabel from "@material-ui/core/FormLabel";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Radio from "@material-ui/core/Radio";
import styles from "./Style/CrawlerStyle";
import ScrapingScenario from "./ScrapingScenario";
import UrlPriority from "./UrlPriority";

const wsUri = "ws://localhost:9000/core/links/async";

const {Map} = require('immutable');

class StreamCrawler extends Component {

    constructor(props) {
        super(props);

        this.state = {
            isConnected: false,
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
        };

        this.socket = new WebSocket(wsUri);
    }

    componentDidMount() {
        this.initSocket();
    }

    initSocket = () => {
        const self = this;

        this.socket.addEventListener('open', function (event) {
            self.setState({
                isConnected: true
            });
            console.log("Connection established");
        });

        this.socket.addEventListener('message', function (event) {
            console.log('Message from server ', event.data);
            self.setState(prevState => ({
                response: [...prevState.response, event.data]
            }))
        });

        this.socket.addEventListener('close', function (event) {
            self.setState({
                isConnected: false
            });
            console.log("Connection closed event code: " + event.code);
        });
    };

    componentWillUnmount() {
        if (this.state.isConnected)
            this.socket.close()
    }

    disconnect = () => {
        if (this.state.isConnected)
            this.socket.close();

    };

    connect = () => {
        if (!this.state.isConnected) {
            this.socket = new WebSocket(wsUri);
            this.initSocket();
        }
    };

    sendRequest = () => {

        const request = JSON.stringify(
            buildRequestBody({
                url: this.state.requestUrl,
                maxSearchDepth: this.state.depth,
                scrapDynamically: this.state.crawlDynamically,
                scrapAllIfNoScenario: this.state.scrapAllIfNoScenario,
                urlPriorities: this.state.urlPriorities.valueSeq().toArray(),
                scenarios: this.state.scenarios.valueSeq().toArray()
            })
        );

        if (request !== null) {
            console.log("sending request: " + request);
            this.socket.send(request);
        }
    };

    clearResponse = () => {
        this.setState({
            response: []
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

    handleCrawlingTypeRadio = (e) => {
        this.setState({
            crawlDynamically: (e.currentTarget.value === "true")
        })
    };

    handleCrawlingWhenNoScenarioRadio = (e) => {
        this.setState({
            scrapAllIfNoScenario: (e.currentTarget.value === "true")
        })
    };

    handleIsTopicalCrawlingRadio = (e) => {
        this.setState(prevState => ({
            isTopicalCrawling: !prevState.isTopicalCrawling
        }))
    };

    handleScenarioChange = (scenarioId, value) => {
        this.setState(prevState => ({scenarios: prevState.scenarios.set(scenarioId, value)}));
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

    handleUrlPriorityChange = (urlPriorityId, value) => {
        this.setState(prevState => ({urlPriorities: prevState.urlPriorities.set(urlPriorityId, value)}));
    };

    getScenariosNames = () => {
        return this.state.scenarios.valueSeq().map(scenario => scenario.name);
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
                                    id="requestUrl"
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

                                <Button
                                    variant="contained"
                                    className={classes.button}
                                    onClick={this.addScrapingScenario}
                                >
                                    Add scenario
                                </Button>

                            </CardContent>
                            <CardContent>
                                <div>
                                    <ol>
                                        {this.state.scrapingScenariosView}
                                    </ol>
                                </div>
                            </CardContent>

                            <h4 className={classes.styleMargin}>Connected: {this.state.isConnected.toString()}</h4>

                            <Button
                                variant="contained"
                                color="primary"
                                className={classes.button}
                                onClick={this.sendRequest}
                            >
                                Send
                                <Icon className={classes.rightIcon}>send</Icon>
                            </Button>
                            <ButtonGroup variant="contained">
                                <Button
                                    onClick={this.connect}
                                >
                                    Connect
                                    <Icon className={classes.rightIcon}>send</Icon>
                                </Button>
                                <Button
                                    onClick={this.disconnect}
                                >
                                    Disconnect
                                    <DeleteIcon className={classes.rightIcon}/>
                                </Button>
                            </ButtonGroup>
                            <Button
                                variant="contained"
                                className={classes.button}
                                onClick={this.clearResponse}
                            >
                                Clear response
                            </Button>


                            <div>
                                <TextField
                                    id="response"
                                    label="Response"
                                    multiline
                                    rows="6"
                                    fullWidth
                                    value={this.state.response}
                                    className={classes.textField}
                                    margin="normal"
                                    variant="outlined"
                                    InputProps={{
                                        readOnly: true,
                                    }}
                                />
                            </div>
                        </Card>
                    </Grid>
                </Grid>
            </>
        );
    }
}

export default withStyles(styles)(StreamCrawler)
