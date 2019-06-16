import React from "react";
import './UrlConfig.css';


class UrlConfig extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            key: props.key,
            close: props.close,
            updateRegex: props.updateRegex,
        }
    }

    render() {
        return(
            <li key={this.state.key} className="nostyle">
                <div className="url-config">
                    <a href="#" data-id={this.state.key} onClick={this.state.close} className="close"/>
                    <div className="url-config-content">
                        <h2 className="url-config-title">Url Config</h2>
                        <div className="regex-input-container">
                            <label>Enter url regex phrase</label>
                            <input className="regex-input" value={this.state.regex} onChange={this.state.updateRegex}/></div>

                    </div>
                </div>
            </li>
        );
    }
}

export default UrlConfig;
