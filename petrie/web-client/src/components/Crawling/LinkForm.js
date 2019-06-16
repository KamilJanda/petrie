import React from "react";
import './LinkForm.css';
import UrlConfig from './UrlConfig';

class LinkForm extends React.Component {
    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.state = {
            response: [],
            urlConfigView: [],
            urlConfiguration: [],
            urlConfigCounter: 0
        }
    }

    handleSubmit(event) {
        event.preventDefault();
        const data = new FormData(event.target);

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
                    "urlConfiguration": this.state.urlConfiguration ? this.state.urlConfiguration : []
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
                        <form onSubmit={this.handleSubmit}>

                            <h1 className="request-header">Crawler</h1>

                            <label htmlFor="url">Enter url</label>
                            <input id="url" name="url" type="text"/>

                            <label htmlFor="depth">Enter depth</label>
                            <input id="depth" name="depth" type="number"/>

                            <button>Crawl data!</button>
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
                            <button onClick={this.addUrlConfig}>Add Url Config</button>
                        </div>
                    </div>
                </div>
                <div>
                    <ol>
                        {this.state.urlConfigView}
                    </ol>
                </div>
            </div>

        );
    }

    addUrlConfig = () => {
        const key = this.state.urlConfigCounter
        const config = {};
        const updateRegex = (event) => {config.regex = event.target.value};
        this.state.urlConfiguration.push(config)
        this.state.urlConfigView.push(<UrlConfig key={key} close={this.deleteUrlConfig} updateRegex={updateRegex}/>);
        this.setState({...this.state, urlConfigCounter: key + 1});
    }

    deleteUrlConfig = (event) => {
        const config = this.state.urlConfigView.find(el => el.key === event.target.dataset.id)
        const index = this.state.urlConfigView.indexOf(config);
        this.state.urlConfigView.splice(index, 1);
        this.state.urlConfiguration.splice(index, 1);
        this.forceUpdate();
    }
}

export default LinkForm;