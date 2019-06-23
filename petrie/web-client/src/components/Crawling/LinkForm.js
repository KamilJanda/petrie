import React from "react";
import './LinkForm.css';
import UrlConfig from './UrlConfig';
import SelectorConfig from "./SelectorConfig";

class LinkForm extends React.Component {
    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.state = {
            response: [],
            urlConfigView: [],
            urlConfiguration: [],
            urlConfigCounter: 0,
            selectorConfigView: [],
            selectorConfiguration: [],
            selectorConfigCounter: 0,
            crawlDynamically: false
        }
    }

    handleSubmit(event) {
        event.preventDefault();
        const data = new FormData(event.target);
        console.log(this.state.crawlDynamically)
        fetch('http://localhost:9000/core/links', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "url": data.get("url"),
                "depth": parseInt(data.get("depth").toString()),
                "configuration": {
                    "urlConfiguration": this.state.urlConfiguration ? this.state.urlConfiguration : [],
                    "selectorConfiguration": this.state.selectorConfiguration ? this.state.selectorConfiguration : [],
                    "searchDynamically": this.state.crawlDynamically
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
    }


    render() {
        return (
            <div>
                <div className="container">
                    <div className="request-container">
                        <form className="custom-form" onSubmit={this.handleSubmit}>

                            <h1 className="request-header">Crawler</h1>

                            <label htmlFor="url">Enter url</label>
                            <input id="url" name="url" type="text"/>

                            <label htmlFor="depth">Enter depth</label>
                            <input id="depth" name="depth" type="number"/>

                            <label htmlFor="depth">Crawling Type</label>
                            <div className="radio-form">
                                <div className="container-radio-left">
                                    <input className="radio-left" type="radio" onChange={this.onRadioChange} checked={this.state.crawlDynamically} value={true}/>
                                    <div className="radio-text">Dynamic</div>
                                </div>
                                <div className="container-radio-right">
                                    <div className="radio-text-right">Async</div>
                                    <input  className="radio-right" type="radio" onChange={this.onRadioChange} checked={!this.state.crawlDynamically} value={false}/>
                                </div>

                            </div>


                            <button>Crawl data! {this.state.searchDynamically}</button>
                        </form>
                    </div>
                    <div className="response-container">
                        <div className="response">
                            <h2>Response</h2>
                            <div className="response-text">
                                {this.state.response}
                            </div>
                        </div>
                    </div>
                    <div className="configuration-container">
                        <div className="configuration">
                            <h2>Configuration</h2>
                            <div className="buttons">
                                <button onClick={this.addUrlConfig}>Add Url Config</button>
                                <button onClick={this.addSelectorConfig}>Add Selector Config</button>
                            </div>

                        </div>
                    </div>
                </div>
                <div>
                    <ol>
                        {this.state.urlConfigView}
                    </ol>
                    <ol>
                        {this.state.selectorConfigView}
                    </ol>
                </div>
            </div>

        );
    }

    addUrlConfig = () => {
        const key = this.state.urlConfigCounter;
        const config = {};
        const updateRegex = (event) => {config.regex = event.target.value};
        this.setState(prevState => ({
            ...this.state,
            urlConfiguration: [...prevState.urlConfiguration, config],
            urlConfigView: [...prevState.urlConfigView,
                <UrlConfig
                    key={key}
                    close={this.deleteConfig(this.state.urlConfigView, this.state.urlConfiguration)}
                    updateRegex={updateRegex}/>
                ],
            urlConfigCounter: key + 1
        }));
    };

    addSelectorConfig = () => {
        const key = this.state.selectorConfigCounter;
        const config = {};
        const updateSelector = (event) => {config.selector = event.target.value};
        this.setState(prevState => ({
            ...this.state,
            selectorConfiguration: [...prevState.urlConfiguration, config],
            selectorConfigView: [...prevState.selectorConfigView,
                <SelectorConfig
                    key={key}
                    close={this.deleteConfig(this.state.selectorConfigView, this.state.selectorConfiguration)}
                    updateRegex={updateSelector}/>
            ],
            selectorConfigCounter: key + 1
        }));
    };

    deleteConfig = (view, configuration) => (event) => {
        const config = view.find(el => el.key === event.target.dataset.id);
        const index = view.indexOf(config);
        view.splice(index, 1);
        configuration.splice(index, 1);
        this.forceUpdate();
    };

    onRadioChange = (e) => this.setState({...this.state, crawlDynamically: e.currentTarget.value == 'true'})

}

export default LinkForm;