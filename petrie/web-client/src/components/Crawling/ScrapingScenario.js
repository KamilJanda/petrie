import React, { Component } from 'react';
import { withStyles } from "@material-ui/core";
import DeleteIcon from '@material-ui/icons/Delete';
import Button from "@material-ui/core/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import TextField from "@material-ui/core/TextField";
import CardContent from "@material-ui/core/CardContent";
import Config from "./Config";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import { Combobox } from "react-widgets";
import Box from "@material-ui/core/Box";
import 'react-widgets/dist/css/react-widgets.css';
import { scenarioBuilder } from "./Utils/RequestBuilder";

const { Map } = require('immutable');

const styles = theme => ({
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
    scenario: {
        marginTop: "40px",
        backgroundColor: "#fff",
        borderRadius: "10px",
        boxShadow: "0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22)",
        position: "relative",
        overflow: "hidden",
        width: "100%",
        maxWidth: "100%",
        minHeight: "150px",
    },
    scenarioContent: {
        marginLeft: "30px",
        marginRight: "30px",
    },
    scenarioName: {
        margin: theme.spacing(1),
    },
    regexInputContainer: {
        margin: theme.spacing(1),
    },
    textField: {
        marginLeft: theme.spacing(1),
        marginRight: theme.spacing(1),
        width: 200,
    },
    buttonGroup: {
        margin: theme.spacing(1),
        marginTop: theme.spacing(3),
    },
    checkbox: {
        margin: theme.spacing(1),
        marginTop: theme.spacing(3),
    },
    combobox: {
        margin: theme.spacing(1),
        marginTop: theme.spacing(3),
    },
    notVisible: {
        display: "none"
    },
    configPartTitle: {
        margin: theme.spacing(1),
        marginTop: theme.spacing(4),
        marginLeft: theme.spacing(1),
    },
    configList: {
        padding: theme.spacing(2),
    }
});

class ScrapingScenario extends Component {

    constructor(props) {
        super(props);

        this.state = {
            name: "",
            elementsToClickView: [],
            elementsToClick: new Map(),
            elementsToClickCounter: 0,
            urlConfigView: [],
            urlConfiguration: new Map(),
            urlConfigCounter: 0,
            selectorConfigView: [],
            selectorConfiguration: new Map(),
            selectorConfigCounter: 0,
            isRootScenario: true,
            isDynamicCrawling: props.isDynamicCrawling,
            isXpathSelector: false
        }
    }

    handleNameChange = (event) => {
        const newName = event.target.value;
        this.setState({ name: newName });
        this.props.onChange(this.props.id, this.buildScenario({ name: newName }))
    };

    handleChangeRootScenario = () => {
        const newIsRootScenario = !this.state.isRootScenario;
        this.setState({ isRootScenario: newIsRootScenario });
        this.props.onChange(this.props.id, this.buildScenario({ isRootScenario: newIsRootScenario }))
    };

    handleChangeIsXpathSelector = () => {
        const newIsXpathSelector = !this.state.isXpathSelector;
        this.setState({ isXpathSelector: newIsXpathSelector });
        this.props.onChange(this.props.id, this.buildScenario({ isXpathSelector: newIsXpathSelector }))
    };

    buildScenario = ({
        name = this.state.name,
        elementsToClick = this.state.elementsToClick,
        selectorConfiguration = this.state.selectorConfiguration,
        urlConfiguration = this.state.urlConfiguration,
        isRootScenario = this.state.isRootScenario,
        isXpathSelector = this.state.isXpathSelector
    } = {}) => {
        return {
            "name": name,
            "preScrapingConfiguration": {
                "elementsToClick": elementsToClick.valueSeq().toArray().map(selector => ({ isXpathSelector: isXpathSelector, selector: selector }))
            },
            "scrapingConfiguration": {
                "elementsToFetchUrlsFrom": selectorConfiguration.valueSeq().toArray().map(selector => ({ isXpathSelector: isXpathSelector, selector: selector }))
            },
            "postScrapingConfiguration": {
                "urlConfiguration": urlConfiguration.valueSeq().toArray().map(regex => ({ regex: regex }))
            },
            "isRootScenario": isRootScenario
        }
    };

    addUrlConfig = () => {
        const key = this.state.urlConfigCounter;

        const update = (itemId, urlConfiguration) => {

            const updatedUrlConfiguration = this.state.urlConfiguration.set(itemId, urlConfiguration);

            this.setState({ urlConfiguration: updatedUrlConfiguration });

            this.props.onChange(this.props.id, this.buildScenario({ urlConfiguration: updatedUrlConfiguration }))
        };

        this.setState(prevState => ({
            urlConfigView: [...prevState.urlConfigView,
            <Config
                key={key}
                id={key}
                type="url"
                configTitle="Url config"
                close={this.deleteUrlConfig}
                update={update}
            />
            ],
            urlConfigCounter: key + 1
        }));
    };

    deleteUrlConfig = (itemId) => {
        const updatedUrlConfigView = this.state.urlConfigView.filter(el => el.key != itemId);
        const updatedUrlConfiguration = this.state.urlConfiguration.delete(itemId);

        this.setState({
            urlConfigView: updatedUrlConfigView,
            urlConfiguration: updatedUrlConfiguration
        });

        this.props.onChange(this.props.id, this.buildScenario({ urlConfiguration: updatedUrlConfiguration }))
    };

    addSelectorConfig = () => {
        const key = this.state.selectorConfigCounter;

        const update = (itemId, selectorConfiguration) => {

            const updatedSelectorConfiguration = this.state.selectorConfiguration.set(itemId, selectorConfiguration);

            this.setState({ selectorConfiguration: updatedSelectorConfiguration });

            this.props.onChange(this.props.id, this.buildScenario({ selectorConfiguration: updatedSelectorConfiguration }))
        };

        this.setState(prevState => ({
            selectorConfigView: [...prevState.selectorConfigView,
            <Config
                key={key}
                id={key}
                type="selector"
                configTitle="Selector config"
                close={this.deleteSelectorConfig}
                update={update}
            />
            ],
            selectorConfigCounter: key + 1
        }));
    };

    deleteSelectorConfig = (itemId) => {
        const updatedSelectorConfigView = this.state.selectorConfigView.filter(el => el.key != itemId);
        const updatedSelectorConfiguration = this.state.selectorConfiguration.delete(itemId);

        this.setState({
            selectorConfigView: updatedSelectorConfigView,
            selectorConfiguration: updatedSelectorConfiguration
        });

        this.props.onChange(this.props.id, this.buildScenario({ selectorConfiguration: updatedSelectorConfiguration }))
    };

    addElementToClick = () => {
        const key = this.state.elementsToClickCounter;

        const update = (itemId, elementToClick) => {

            const updatedElementsToClick = this.state.elementsToClick.set(itemId, elementToClick);

            this.setState({ elementsToClick: updatedElementsToClick });

            this.props.onChange(this.props.id, this.buildScenario({ elementsToClick: updatedElementsToClick }))
        };

        this.setState(prevState => ({
            elementsToClickView: [...prevState.elementsToClickView,
            <Config
                key={key}
                id={key}
                type="elementsToClick"
                configTitle="Element to click"
                close={this.deleteElementToClick}
                update={update}
            />
            ],
            elementsToClickCounter: key + 1
        }));
    };

    deleteElementToClick = (itemId) => {
        const updatedConfigView = this.state.elementsToClickView.filter(el => el.key != itemId);
        const updatedElementsToClick = this.state.elementsToClick.delete(itemId);

        this.setState({
            elementsToClickView: updatedConfigView,
            elementsToClick: updatedElementsToClick
        });

        this.props.onChange(this.props.id, this.buildScenario({ elementsToClick: updatedElementsToClick }))
    };

    getScenariosNames = () => {
        return this.props.getScenariosNames();
    };

    titleIfNonEmpty(title, array) {
        const { classes } = this.props;
        if (array.length > 0) {
            return (<div className={classes.configPartTitle}><h2>{title}</h2></div>)
        } else {
            return (<div></div>)
        }
    }

    render() {
        const { classes } = this.props;

        return (
            <li key={this.props.id} className={classes.noStyle}>
                <div className={classes.scenario}>

                    <DeleteIcon data-id={this.props.id} className={classes.icon}
                        onClick={() => this.props.close(this.props.id)}
                    />

                    <CardContent>
                        <TextField
                            id="scenarioName"
                            label="Scenario Name"
                            className={classes.textField}
                            onChange={this.handleNameChange}
                            margin="normal"
                        />

                        <FormControlLabel
                            className={classes.checkbox}
                            control={
                                <Checkbox
                                    checked={this.state.isRootScenario}
                                    onChange={this.handleChangeRootScenario}
                                    value="checkedB"
                                    color="primary"
                                />
                            }
                            label="is Root Scenario"
                        />

                        <FormControlLabel
                            className={classes.checkbox}
                            control={
                                <Checkbox
                                    checked={this.state.isXpathSelector}
                                    onChange={this.handleChangeIsXpathSelector}
                                    value="checkedB"
                                    color="primary"
                                />
                            }
                            label="is Xpath selector"
                        />

                        <Box className={classes.combobox}>
                            <label>Target Scenario </label>
                            <Combobox
                                onChange={this.onTargetChange}
                                data={this.getScenariosNames()}
                                onToggle={() => {
                                    this.forceUpdate();
                                }}
                            />
                        </Box>

                        <ButtonGroup
                            variant="contained"
                            className={classes.buttonGroup}
                        >
                            <Button
                                className={this.props.isDynamicCrawling ? '' : classes.notVisible}
                                onClick={this.addElementToClick}
                            >
                                Add PreScraping Config
                            </Button>
                            <Button
                                onClick={this.addSelectorConfig}
                            >
                                Add Scraping Config
                            </Button>
                            <Button
                                onClick={this.addUrlConfig}
                            >
                                Add PostScraping Config
                            </Button>
                        </ButtonGroup>

                    </CardContent>

                    <div className={classes.configList}>
                        <ol className={(this.props.isDynamicCrawling ? '' : classes.notVisible)}>
                            {this.titleIfNonEmpty("PreScraping Configuration: ", this.state.elementsToClickView)}
                            {this.state.elementsToClickView}
                        </ol>
                        <ol>
                            {this.titleIfNonEmpty("Scraping Configuration: ", this.state.selectorConfigView)}
                            {this.state.selectorConfigView}
                        </ol>
                        <ol>
                            {this.titleIfNonEmpty("PostScraping Configuration: ", this.state.urlConfigView)}
                            {this.state.urlConfigView}
                        </ol>
                    </div>

                </div>
            </li>
        );
    }
}

export default withStyles(styles)(ScrapingScenario)
