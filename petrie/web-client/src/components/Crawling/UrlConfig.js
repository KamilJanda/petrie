import React from "react";
import './CommonConfig.css';


class UrlConfig extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return(
            <li key={this.props.key} className="nostyle">
                <div className="config">
                    <a href="#" data-id={this.props.key} onClick={this.props.close} className="close"/>
                    <div className="config-content">
                        <h2 className="config-title">Url Config</h2>
                        <div className="regex-input-container">
                            <label>Enter url regex phrase</label>
                            <input className="regex-input" value={this.props.regex} onChange={this.props.updateRegex}/></div>
                    </div>
                </div>
            </li>
        );
    }
}

export default UrlConfig;
