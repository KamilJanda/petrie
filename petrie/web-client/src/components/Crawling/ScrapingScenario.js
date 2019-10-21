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
import WriteIntoFieldConfig from "./WriteIntoFieldConfig";

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
        margin: theme.spacing(3)
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
        paddingTop: theme.spacing(0)
    }
});

class ScrapingScenario extends Component {

    constructor(props) {
        super(props);

        this.state = {
            name: "",
            preScrapingOrdering: 0,
            elementsToClickView: [],
            elementsToClick: new Map(),
            elementsToClickCounter: 0,
            elementsToScrollToView: [],
            elementsToScrollTo: new Map(),
            elementsToToScrollToCounter: 0,
            waitTimeoutView: [],
            waitTimeout: new Map(),
            waitTimeoutCounter: 0,
            writeToElementView: [],
            writeToElement: new Map(),
            writeToElementCounter: 0,
            urlConfigView: [],
            urlConfiguration: new Map(),
            urlConfigCounter: 0,
            elementsToFetchUrlFromSelectorConfigView: [],
            elementsToFetchUrlFromSelectorConfiguration: new Map(),
            elementsToFetchUrlsFromSelectorConfigCounter: 0,
            elementsToFetchTextFromSelectorConfigView: [],
            elementsToFetchTextFromSelectorConfiguration: new Map(),
            elementsToFetchTextFromSelectorConfigCounter: 0,
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
        elementsToScrollTo = this.state.elementsToScrollTo,
        waitTimeout = this.state.waitTimeout,
        writeToElement = this.state.writeToElement,
        elementsToFetchUrlFromSelectorConfiguration = this.state.elementsToFetchUrlFromSelectorConfiguration,
        elementsToFetchTextFromSelectorConfiguration = this.state.elementsToFetchTextFromSelectorConfiguration,
        urlConfiguration = this.state.urlConfiguration,
        isRootScenario = this.state.isRootScenario,
        isXpathSelector = this.state.isXpathSelector
    } = {}) => {
        return {
            "name": name,
            "preScrapingConfiguration": {
                "preScrapingConfigurationElementsViews": this.preparePreScrapingConfiguration(
                    isXpathSelector,
                    elementsToClick,
                    elementsToScrollTo,
                    waitTimeout,
                    writeToElement
                )
            },
            "scrapingConfiguration": {
                "elementsToFetchUrlsFrom": elementsToFetchUrlFromSelectorConfiguration.valueSeq().toArray().map(selector => ({ isXpathSelector: isXpathSelector, selector: selector })),
                "elementsToScrapContentFrom": elementsToFetchTextFromSelectorConfiguration.valueSeq().toArray().map(selector => ({ isXpathSelector: isXpathSelector, selector: selector }))
            },
            "postScrapingConfiguration": {
                "urlConfiguration": urlConfiguration.valueSeq().toArray().map(regex => ({ regex: regex }))
            },
            "isRootScenario": isRootScenario
        }
    };

    preparePreScrapingConfiguration = (
        isXpathSelector,
        elementsToClick,
        elementsToScrollTo,
        waitTimeout,
        writeToElement
    ) => {
        const toClick =
            elementsToClick.valueSeq().toArray().map(el => (
                {"selector": { isXpathSelector: isXpathSelector, selector: el.selector }, "actionType": "ElementToClickView", ord: el.ord}
            )) || [];
        const toScroll = elementsToScrollTo.valueSeq().toArray().map(el => (
                {"selector": { isXpathSelector: isXpathSelector, selector: el.selector }, "actionType": "ScrollToElementView", ord: el.ord}
            )) || [];
        const timeouts = waitTimeout.valueSeq().toArray().map(el => ({ timeout: parseInt(el.timeout),  "actionType": "WaitTimeoutView", ord: el.ord})) || [];
        const toWriteTo = writeToElement.valueSeq().toArray().map(el => ({  "selector": { isXpathSelector: isXpathSelector, selector: el.selector }, text: el.text,  "actionType": "WriteToElementView", ord: el.ord})) || [];
        const values = toClick.concat(toScroll, timeouts, toWriteTo);
        const sorted = values.sort((el1, el2) => el1.ord - el2.ord);
        sorted.forEach(el => delete el.ord);
        return sorted;
    }

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
                label={"regex"}
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
        const key = this.state.elementsToFetchUrlsFromSelectorConfigCounter;

        const update = (itemId, elementsToFetchUrlFromSelectorConfiguration) => {

            const updatedSelectorConfiguration = this.state.elementsToFetchUrlFromSelectorConfiguration.set(itemId, elementsToFetchUrlFromSelectorConfiguration);

            this.setState({ elementsToFetchUrlFromSelectorConfiguration: updatedSelectorConfiguration });

            this.props.onChange(this.props.id, this.buildScenario({ elementsToFetchUrlFromSelectorConfiguration: updatedSelectorConfiguration }))
        };

        this.setState(prevState => ({
            elementsToFetchUrlFromSelectorConfigView: [...prevState.elementsToFetchUrlFromSelectorConfigView,
            <Config
                label={"selector"}
                key={key}
                id={key}
                type="selector"
                configTitle="Url selector config"
                close={this.deleteSelectorConfig}
                update={update}
            />
            ],
            elementsToFetchUrlsFromSelectorConfigCounter: key + 1
        }));
    };

    deleteSelectorConfig = (itemId) => {
        const updatedSelectorConfigView = this.state.elementsToFetchUrlFromSelectorConfigView.filter(el => el.key != itemId);
        const updatedSelectorConfiguration = this.state.elementsToFetchUrlFromSelectorConfiguration.delete(itemId);

        this.setState({
            elementsToFetchUrlFromSelectorConfigView: updatedSelectorConfigView,
            elementsToFetchUrlFromSelectorConfiguration: updatedSelectorConfiguration
        });

        this.props.onChange(this.props.id, this.buildScenario({ elementsToFetchUrlFromSelectorConfiguration: updatedSelectorConfiguration }))
    };

    addTextSelectorConfig = () => {
        const key = this.state.elementsToFetchTextFromSelectorConfigCounter;

        const update = (itemId, elementsToFetchTextFromSelectorConfiguration) => {

            const updatedSelectorConfiguration = this.state.elementsToFetchTextFromSelectorConfiguration.set(itemId, elementsToFetchTextFromSelectorConfiguration);

            this.setState({ elementsToFetchTextFromSelectorConfiguration: updatedSelectorConfiguration });

            this.props.onChange(this.props.id, this.buildScenario({ elementsToFetchTextFromSelectorConfiguration: updatedSelectorConfiguration }))
        };

        this.setState(prevState => ({
            elementsToFetchTextFromSelectorConfigView: [...prevState.elementsToFetchTextFromSelectorConfigView,
                <Config
                    label={"selector"}
                    key={key}
                    id={key}
                    type="selector"
                    configTitle="Text selector config"
                    close={this.deleteTextSelectorConfig}
                    update={update}
                />
            ],
            elementsToFetchTextFromSelectorConfigCounter: key + 1
        }));
    };

    deleteTextSelectorConfig = (itemId) => {
        const updatedSelectorConfigView = this.state.elementsToFetchTextFromSelectorConfigView.filter(el => el.key != itemId);
        const updatedSelectorConfiguration = this.state.elementsToFetchTextFromSelectorConfiguration.delete(itemId);

        this.setState({
            elementsToFetchTextFromSelectorConfigView: updatedSelectorConfigView,
            elementsToFetchTextFromSelectorConfiguration: updatedSelectorConfiguration
        });

        this.props.onChange(this.props.id, this.buildScenario({ elementsToFetchTextFromSelectorConfiguration: updatedSelectorConfiguration }))
    };

    addElementToClick = () => {
        const key = this.state.elementsToClickCounter;

        const ord = this.state.preScrapingOrdering;

        const update = (itemId, elementToClick) => {

            const updatedElementsToClick = this.state.elementsToClick.set(itemId, {selector: elementToClick, ord: ord});

            this.setState({ elementsToClick: updatedElementsToClick });

            this.props.onChange(this.props.id, this.buildScenario({ elementsToClick: updatedElementsToClick }))
        };

        this.setState(prevState => ({
            elementsToClickView: [...prevState.elementsToClickView,
            <Config
                label={"selector"}
                key={key}
                id={key}
                type="elementsToClick"
                configTitle="Element to click"
                close={this.deleteElementToClick}
                update={update}
            />
            ],
            elementsToClickCounter: key + 1,
            preScrapingOrdering: ord + 1
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

    addElementToScrollTo = () => {
        const key = this.state.elementsToToScrollToCounter;

        const ord = this.state.preScrapingOrdering;

        const update = (itemId, elementToScrollTo) => {

            const updatedElementsToScrollTo = this.state.elementsToScrollTo.set(itemId, {selector: elementToScrollTo, ord: ord});

            this.setState({ elementsToScrollTo: updatedElementsToScrollTo });

            this.props.onChange(this.props.id, this.buildScenario({ elementsToScrollTo: updatedElementsToScrollTo }))
        };

        this.setState(prevState => ({
            elementsToScrollToView: [...prevState.elementsToScrollToView,
                <Config
                    label={"selector"}
                    key={key}
                    id={key}
                    type="elementsToScrollTo"
                    configTitle="Element to scroll to"
                    close={this.deleteElementToScrollTo}
                    update={update}
                />
            ],
            elementsToToScrollToCounter: key + 1,
            preScrapingOrdering: ord + 1
        }));
    };

    deleteElementToScrollTo = (itemId) => {
        const updatedElementsToScrollToView = this.state.elementsToScrollToView.filter(el => el.key != itemId);
        const updatedElementsToScrollTo = this.state.elementsToScrollTo.delete(itemId);

        this.setState({
            elementsToScrollToView: updatedElementsToScrollToView,
            elementsToScrollTo: updatedElementsToScrollTo
        });

        this.props.onChange(this.props.id, this.buildScenario({ elementsToScrollTo: updatedElementsToScrollTo }))
    };

    addWaitTimeout = () => {
        const ord = this.state.preScrapingOrdering;

        const key = this.state.waitTimeoutCounter;

        const update = (itemId, waitTimeout) => {

            const updatedWaitTimeout = this.state.waitTimeout.set(itemId, { timeout: waitTimeout, ord: ord });

            this.setState({ waitTimeout: updatedWaitTimeout });

            this.props.onChange(this.props.id, this.buildScenario({ waitTimeout: updatedWaitTimeout }))
        };

        this.setState(prevState => ({
            waitTimeoutView: [...prevState.waitTimeoutView,
                <Config
                    label={"timeout"}
                    key={key}
                    id={key}
                    type="waitTimeout"
                    configTitle="Wait Timeout"
                    close={this.deleteWaitTimeout}
                    update={update}
                />
            ],
            waitTimeoutCounter: key + 1,
            preScrapingOrdering: ord + 1
        }));
    };

    deleteWaitTimeout = (itemId) => {
        const updatedWaitTimeoutView = this.state.waitTimeoutView.filter(el => el.key != itemId);
        const updatedWaitTimeout = this.state.waitTimeout.delete(itemId);

        this.setState({
            waitTimeoutView: updatedWaitTimeoutView,
            waitTimeout: updatedWaitTimeout
        });

        this.props.onChange(this.props.id, this.buildScenario({ waitTimeout: updatedWaitTimeout }))
    };

    addWriteToElement = () => {
        const ord = this.state.preScrapingOrdering;

        const key = this.state.writeToElementCounter;

        const update = (itemId, writeToElement) => {

            const updatedWriteToElement = this.state.writeToElement.set(itemId, {ord: ord, ...writeToElement});

            this.setState({ writeToElement: updatedWriteToElement });

            this.props.onChange(this.props.id, this.buildScenario({ writeToElement: updatedWriteToElement }))
        };

        this.setState(prevState => ({
            writeToElementView: [...prevState.writeToElementView,
                <WriteIntoFieldConfig
                    key={key}
                    id={key}
                    type="writeInto"
                    configTitle="Write Into Element"
                    close={this.deleteWriteToElement}
                    update={update}
                />
            ],
            writeToElementCounter: key + 1,
            preScrapingOrdering: ord + 1
        }));
    };

    deleteWriteToElement = (itemId) => {
        const updatedWaitTimeoutView = this.state.waitTimeoutView.filter(el => el.key != itemId);
        const updatedWaitTimeout = this.state.waitTimeout.delete(itemId);

        this.setState({
            waitTimeoutView: updatedWaitTimeoutView,
            waitTimeout: updatedWaitTimeout
        });

        this.props.onChange(this.props.id, this.buildScenario({ waitTimeout: updatedWaitTimeout }))
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

                        <div> PreScraping Options: </div>
                        <ButtonGroup
                            variant="contained"
                            className={classes.buttonGroup}
                        >
                            <Button
                                className={this.props.isDynamicCrawling ? '' : classes.notVisible}
                                onClick={this.addElementToClick}
                            >
                                Add Element to click Config
                            </Button>
                            <Button
                                className={this.props.isDynamicCrawling ? '' : classes.notVisible}
                                onClick={this.addElementToScrollTo}
                            >
                                Add Element to scroll to Config
                            </Button>
                            <Button
                                className={this.props.isDynamicCrawling ? '' : classes.notVisible}
                                onClick={this.addWaitTimeout}
                            >
                                Add Wait Timeout Config
                            </Button>
                            <Button
                                className={this.props.isDynamicCrawling ? '' : classes.notVisible}
                                onClick={this.addWriteToElement}
                            >
                                Add Write To Element Config
                            </Button>
                        </ButtonGroup>
                        <div> Scraping Options: </div>
                        <ButtonGroup
                            variant="contained"
                            className={classes.buttonGroup}
                        >
                            <Button
                                onClick={this.addSelectorConfig}
                            >
                                Add Url Scraping Config
                            </Button>
                            <Button
                                onClick={this.addTextSelectorConfig}
                            >
                                Add Text Scraping Config
                            </Button>
                        </ButtonGroup>
                        <div> PostScraping Options: </div>
                        <ButtonGroup
                            variant="contained"
                            className={classes.buttonGroup}
                        >
                            <Button
                                onClick={this.addUrlConfig}
                            >
                                Add Url Match Regex Config
                            </Button>
                        </ButtonGroup>

                    </CardContent>

                    <div className={classes.configList}>
                        <ol className={(this.props.isDynamicCrawling ? '' : classes.notVisible)}>
                            {this.titleIfNonEmpty("PreScraping Configuration: ", this.state.elementsToClickView + this.state.elementsToScrollToView + this.state.waitTimeoutView)}
                            {this.state.elementsToClickView}
                            {this.state.elementsToScrollToView}
                            {this.state.waitTimeoutView}
                            {this.state.writeToElementView}
                        </ol>
                        <ol>
                            {this.titleIfNonEmpty("Scraping Configuration: ", this.state.elementsToFetchUrlFromSelectorConfigView)}
                            {this.state.elementsToFetchUrlFromSelectorConfigView}
                            {this.state.elementsToFetchTextFromSelectorConfigView}
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
