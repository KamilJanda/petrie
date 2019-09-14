import React from "react";
import {Grid, withStyles} from "@material-ui/core";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import TextField from "@material-ui/core/TextField";
import Icon from "@material-ui/core/Icon";
import Button from "@material-ui/core/Button";
import Config from "./Config";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import RadioGroup from "@material-ui/core/RadioGroup";
import Radio from "@material-ui/core/Radio";
import FormLabel from "@material-ui/core/FormLabel";
import styles from "./Style/CrawlerStyle";

const requestUrl = "http://localhost:9000/core/links";

class SimpleCrawler extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            response: [],
            requestUrl: "",
            depth: 0,
            urlConfigView: [],
            urlConfiguration: [],
            urlConfigCounter: 0,
            selectorConfigView: [],
            selectorConfiguration: [],
            selectorConfigCounter: 0,
            crawlDynamically: false
        }
    }

    sendRequest = () => {

        fetch(requestUrl, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "url": this.state.requestUrl,
                "configuration": {
                    "maxSearchDepth": parseInt(this.state.depth),
                    "scrapAllIfNoScenario": true,
                    "scrapDynamically": this.state.crawlDynamically,
                    "scenarios": [{
                        "name": "default",
                        "preScrapingConfiguration": {
                            "elementsToClick": []
                        },
                        "scrapingConfiguration": {
                            "elementsToFetchUrlsFrom":  this.state.selectorConfiguration ? this.state.selectorConfiguration : []
                        },
                        "postScrapingConfiguration": {
                            "urlConfiguration": this.state.urlConfiguration ? this.state.urlConfiguration : []
                        },
                        "isRootScenario": true
                    }]
                }
            })

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

    addUrlConfig = () => {
        const key = this.state.urlConfigCounter;
        const config = {};
        const updateRegex = (event) => {
            config.regex = event.target.value
        };

        this.setState(prevState => ({
            urlConfiguration: [...prevState.urlConfiguration, config],
            urlConfigView: [...prevState.urlConfigView,
                <Config
                    key={key}
                    id={key}
                    type="url"
                    configTitle="Url config"
                    close={this.deleteUrlConfig}
                    update={updateRegex}/>
            ],
            urlConfigCounter: key + 1
        }));
    };

    addSelectorConfig = () => {
        const key = this.state.selectorConfigCounter;
        const config = {};
        const updateSelector = (event) => {
            config.selector = event.target.value
        };

        this.setState(prevState => ({
            selectorConfiguration: [...prevState.urlConfiguration, config],
            selectorConfigView: [...prevState.selectorConfigView,
                <Config
                    key={key}
                    id={key}
                    type="selector"
                    configTitle="Selector config"
                    close={this.deleteSelectorConfig}
                    update={updateSelector}/>
            ],
            selectorConfigCounter: key + 1
        }));
    };

    deleteUrlConfig = (itemId) => {
        const updatedUrlConfigView = this.state.urlConfigView.filter(el => el.key != itemId);

        this.setState({
            urlConfigView: updatedUrlConfigView
        })
    };

    deleteSelectorConfig = (itemId) => {
        const updatedSelectorConfigView = this.state.selectorConfigView.filter(el => el.key != itemId);

        this.setState({
            selectorConfigView: updatedSelectorConfigView
        })
    };

    handleCrawlingTypeRadio = (e) => {
        this.setState({
            crawlDynamically: (e.currentTarget.value === "true")
        })
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
                                    label="Request depth"
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
                                    value={this.state.crawlDynamically.toString()}
                                    onChange={this.handleCrawlingTypeRadio}
                                >
                                    <FormControlLabel value="true" control={<Radio color="primary"/>}
                                                      label="Dynamic"/>
                                    <FormControlLabel value="false" control={<Radio color="primary"/>}
                                                      label="Async"/>
                                </RadioGroup>

                                <Button
                                    variant="contained"
                                    color="primary"
                                    className={classes.button}
                                    onClick={this.sendRequest}
                                >
                                    Crawl data {this.crawType()}!
                                    <Icon className={classes.rightIcon}>send</Icon>
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

                                <ButtonGroup
                                    variant="contained"
                                    className={classes.buttonGroup}
                                >
                                    <Button
                                        onClick={this.addUrlConfig}
                                    >
                                        Add Url Config
                                    </Button>
                                    <Button
                                        onClick={this.addSelectorConfig}
                                    >
                                        Add Selector Config
                                    </Button>
                                </ButtonGroup>
                            </CardContent>

                            <CardContent>
                                <div>
                                    <ol>
                                        {this.state.urlConfigView}
                                    </ol>
                                    <ol>
                                        {this.state.selectorConfigView}
                                    </ol>
                                </div>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
            </>
        );
    }
}

export default withStyles(styles)(SimpleCrawler);
