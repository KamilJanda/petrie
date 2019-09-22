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
            scenarios: [],
            scenariosViews: [],
            scenarioCounter: 0,
            crawlDynamically: false,
            scrapAllIfNoScenario: true
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
                    "scrapAllIfNoScenario": this.state.scrapAllIfNoScenario,
                    "scrapDynamically": this.state.crawlDynamically,
                    "scenarios": this.state.scenarios
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

    getScenariosNames = () => {
        return this.state.scenarios
            .map(scenario => scenario.name)
            .filter(name => name != "")
    }


    addScenario = () => {
        const key = this.state.scenarioCounter;
        const scenario = {};
        const updateScenario = (newScenario) => {
            for (var prop in newScenario) {
                if (newScenario.hasOwnProperty(prop)) {
                    scenario[prop] = newScenario[prop];
                }
            }
        };

        this.setState(prevState => ({
            scenarios: [...prevState.scenarios, scenario],
            scenariosViews: [...prevState.scenariosViews,
                <Scenario
                    key={key}
                    id={key}
                    close={this.deleteScenario}
                    isDynamicCrawling={this.state.crawlDynamically}
                    updateScenario={updateScenario}
                    getScenariosNames = {this.getScenariosNames}/>
            ],
            scenarioCounter: key + 1
        }));
    };

    deleteScenario = (scenarioOrdinal) => {
        const updatedScenariosViews = this.state.scenariosViews.filter(el => el.key != scenarioOrdinal);
        const updatedScenarios = this.state.scenarios.filter(el => el.key != scenarioOrdinal);

        this.setState({
            scenariosViews: updatedScenariosViews,
            scenarios: updatedScenarios,
        })
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
                                    label="Max request depth"
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
                                                      label="Scrap all"/>
                                    <FormControlLabel value="false" control={<Radio color="primary"/>}
                                                      label="Scrap none"/>
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
                                    className={classes.buttonGroup}>
                                    <Button
                                        onClick={this.addScenario}>
                                        Add Scenario
                                    </Button>
                                </ButtonGroup>
                            </CardContent>

                            <CardContent>
                                <div>
                                    <ol>
                                        {this.state.scenariosViews}
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
