import React from "react";
import {makeStyles, withStyles} from "@material-ui/core";
import DeleteIcon from '@material-ui/icons/Delete';
import CardContent from "@material-ui/core/CardContent";
import Card from "@material-ui/core/Card";
import Button from "@material-ui/core/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Config from "./Config";
import TextField from "@material-ui/core/TextField";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Radio from "@material-ui/core/Radio";
import { Combobox } from 'react-widgets'
import 'react-widgets/dist/css/react-widgets.css';


const styles = theme => ({
        configPartTitle: {
          paddingTop: "30px",
        },
        buttonGroup: {
            margin: theme.spacing(1),
        },
        root: {
            color: theme.palette.text.primary,
        },
        icon: {
            margin: theme.spacing(1),
            fontSize: 32,
            position: "absolute",
            top: "0",
            right: "0",
        },
        noStyle: {
            listStyleType: "none",
        },
        config: {
            marginTop: "40px",
            backgroundColor: "#fff",
            borderRadius: "10px",
            boxShadow: "0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22)",
            position: "relative",
            overflow: "hidden",
            width: "90%",
            maxWidth: "100%",
            minHeight: "150px",
        },
        notVisible: {
            display: "none"
        }

    })

class Scenario extends React.Component {

    constructor(props) {
        super(props);
        this.getScenariosNames = props.getScenariosNames;
        this.id = props.id;
        this.key = props.key;
        this.close = props.close;
        this.updateScenario = props.updateScenario;
        this.state = {
            isDynamicCrawling: props.isDynamicCrawling,
            scenario: {
                name: "",
                isRootScenario: false,
                preScrapingConfiguration: {
                    elementsToClick: []
                },
                scrapingConfiguration: {
                    elementsToFetchUrlsFrom: []
                },
                postScrapingConfiguration: {
                    urlConfiguration: []
                }
            },
            view: {
                preScrapingConfigurationView: [],
                scrapingConfigurationView: [],
                postScrapingConfigurationView: []
            },
            counters: {
                preScrapingConfigurationCounter: 0,
                scrapingConfigurationCounter: 0,
                postScrapingConfigurationCounter: 0,
            }
        };
    }

    addPreScrapingConfig = () => {
        const key = this.state.counters.preScrapingConfigurationCounter;
        const config = {
            isXpathSelector: false
        };
        const update = (event) => {
            config.selector = event.target.value
        };

        this.setState(prevState => ({
            scenario: {
                ...prevState.scenario,
                preScrapingConfiguration: {
                    elementsToClick: [...prevState.scenario.preScrapingConfiguration.elementsToClick, config]
                },

            },
            counters: {
                ...prevState.counters,
                preScrapingConfigurationCounter: prevState.counters.preScrapingConfigurationCounter + 1
            },
            view: {
                ...prevState.view,
                preScrapingConfigurationView: [...prevState.view.preScrapingConfigurationView,
                    <Config
                        label="enter css selector"
                        key={key}
                        id={key}
                        type="css selector"
                        configTitle="Element to Click"
                        close={this.deletePreScrapingConfig}
                        update={update}/>
                    ]
            }
        }), () => this.updateScenario(this.state.scenario));
    };

    deletePreScrapingConfig = (itemOrd) => {
        const updatedView = this.state.view.preScrapingConfigurationView.filter(el => el.key != itemOrd);
        const updatedElementsToClick = this.state.scenario.preScrapingConfiguration.elementsToClick.filter(el => el.key != itemOrd);
        this.setState(prevState => ({
            view: {
                ...prevState.view,
                preScrapingConfigurationView: updatedView
            },
            scenario: {
                ...prevState.scenario,
                preScrapingConfiguration: {
                    elementsToClick: updatedElementsToClick
                },

            },
        }), () => this.updateScenario(this.state.scenario))
    };

    addScrapingConfig = () => {
        const key = this.state.counters.scrapingConfigurationCounter;
        const config = {
            isXpathSelector: false
        };
        const update = (event) => {
            config.selector = event.target.value
        };

        this.setState(prevState => ({
            scenario: {
                ...prevState.scenario,
                scrapingConfiguration: {
                    elementsToFetchUrlsFrom: [...prevState.scenario.scrapingConfiguration.elementsToFetchUrlsFrom, config]
                },

            },
            counters: {
                ...prevState.counters,
                scrapingConfigurationCounter: prevState.counters.scrapingConfigurationCounter + 1
            },
            view: {
                ...prevState.view,
                scrapingConfigurationView: [...prevState.view.scrapingConfigurationView,
                    <Config
                        label="enter css selector"
                        key={key}
                        id={key}
                        type="css selector"
                        configTitle="Element to Scrap Url From"
                        close={this.deleteScrapingConfig}
                        update={update}/>
                ]
            }
        }), () => this.updateScenario(this.state.scenario));
    };

    deleteScrapingConfig = (itemOrd) => {
        const updatedView = this.state.view.scrapingConfigurationView.filter(el => el.key != itemOrd);
        const updatedElementsToClick = this.state.scenario.scrapingConfiguration.elementsToFetchUrlsFrom.filter(el => el.key != itemOrd);
        this.setState(prevState => ({
            view: {
                ...prevState.view,
                scrapingConfigurationView: updatedView
            },
            scenario: {
                ...prevState.scenario,
                scrapingConfiguration: {
                    elementsToFetchUrlsFrom: updatedElementsToClick
                },

            },
        }), () => this.updateScenario(this.state.scenario))
    };

    addPostScrapingConfig = () => {
        const key = this.state.counters.postScrapingConfigurationCounter;
        const config = {};
        const update = (event) => {
            config.regex = event.target.value
        };

        this.setState(prevState => ({
            scenario: {
                ...prevState.scenario,
                postScrapingConfiguration: {
                    urlConfiguration: [...prevState.scenario.postScrapingConfiguration.urlConfiguration, config]
                },

            },
            counters: {
                ...prevState.counters,
                postScrapingConfigurationCounter: prevState.counters.postScrapingConfigurationCounter + 1
            },
            view: {
                ...prevState.view,
                postScrapingConfigurationView: [...prevState.view.postScrapingConfigurationView,
                    <Config
                        label="enter regex"
                        key={key}
                        id={key}
                        type="regex"
                        configTitle="Url Regex"
                        close={this.deletePostScrapingConfig}
                        update={update}/>]
            }
        }), () => this.updateScenario(this.state.scenario));
    };

    deletePostScrapingConfig = (itemOrd) => {
        const updatedView = this.state.view.postScrapingConfigurationView.filter(el => el.key != itemOrd);
        const updatedElementsToClick = this.state.scenario.postScrapingConfiguration.urlConfiguration.filter(el => el.key != itemOrd);
        this.setState(prevState => ({
            view: {
                ...prevState.view,
                postScrapingConfigurationView: updatedView
            },
            scenario: {
                ...prevState.scenario,
                postScrapingConfiguration: {
                    urlConfiguration: updatedElementsToClick
                },

            },
        }), () => this.updateScenario(this.state.scenario))
    };

    handleIsRootScenario = (event) => {
        const value = (event.currentTarget.value === "true")
        this.setState(prevState => ({
            scenario: {
                ...prevState.scenario,
                isRootScenario: value
            }
        }), () => this.updateScenario(this.state.scenario));
    };

    titleIfNonEmpty(title, array) {
        const {classes} = this.props;
        if (array.length > 0) {
            return (<div className={classes.configPartTitle}><h2>{title}</h2></div>)
        } else {
            return (<div></div>)
        }
    }

    updateName = (event) => {
        const value = event.currentTarget.value;
        this.setState(prevState => ({
            scenario: {
                ...prevState.scenario,
                name: value
            }
        }), () => this.updateScenario(this.state.scenario));

    };

    render () {
        const {classes} = this.props;
         return (
             <li key={this.state.id} className={classes.noStyle}>
                <div className={classes.config}>
                    <DeleteIcon data-id={this.state.id} className={classes.icon} onClick={() => this.close(this.id)}/>
                        <CardContent>
                            <TextField
                                id="scenarioName"
                                label="Scenario name"
                                value={this.state.scenario.name}
                                fullWidth
                                className={classes.textField}
                                margin="normal"
                                onChange={this.updateName}
                                InputLabelProps={{
                                    shrink: true,
                                }}

                            />
                            <label>Target Scenario </label>
                            <Combobox
                                onChange={this.onTargetChange}
                                data={this.getScenariosNames()}
                                onToggle={()=>{ this.forceUpdate();}}
                            />
                            <label>Is Root Scenario? </label>
                            <RadioGroup
                                aria-label="Crawling Type"
                                name="Crawling Type"
                                className={classes.group}
                                value={this.state.scenario.isRootScenario.toString()}
                                onChange={this.handleIsRootScenario}>
                                <FormControlLabel value="true" control={<Radio color="primary"/>}
                                                  label="Root Scenario"/>
                                <FormControlLabel value="false" control={<Radio color="primary"/>}
                                                  label="Non Root Scenario"/>
                            </RadioGroup>
                            <ButtonGroup
                                variant="contained"
                                className={classes.buttonGroup}>
                                <Button
                                    className={this.state.isDynamicCrawling ? '' : classes.notVisible}
                                    onClick={this.addPreScrapingConfig}>
                                    Add PreScraping Config
                                </Button>
                                <Button
                                    onClick={this.addScrapingConfig}>
                                    Add Scraping Config
                                </Button>
                                <Button
                                    onClick={this.addPostScrapingConfig}>
                                    Add PostScraping Config
                                </Button>
                            </ButtonGroup>
                        </CardContent>
                        <CardContent>
                            <div>
                                <ol className={(this.state.isDynamicCrawling ? '' : classes.notVisible)}>
                                    {this.titleIfNonEmpty("PreScraping Configuration: ", this.state.view.preScrapingConfigurationView)}
                                    {this.state.view.preScrapingConfigurationView}
                                </ol>
                                <ol>
                                    {this.titleIfNonEmpty("Scraping Configuration: ", this.state.view.scrapingConfigurationView)}
                                    {this.state.view.scrapingConfigurationView}
                                </ol>
                                <ol>
                                    {this.titleIfNonEmpty("PostScraping Configuration: ", this.state.view.postScrapingConfigurationView)}
                                    {this.state.view.postScrapingConfigurationView}
                                </ol>
                            </div>
                        </CardContent>
                </div>
            </li>
         )
    };

}

export default withStyles(styles)(Scenario);
