import React from "react";
import './CommonConfig.css';


class SelectorConfig extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return(
            <li key={this.props.key} className="nostyle">
                <div className="config">
                    <a href="#" data-id={this.props.key} onClick={this.props.close} className="close"/>
                    <div className="config-content">
                        <h2 className="config-title">Selector Config</h2>
                        <div className="regex-input-container">
                            <label>Enter selector regex</label>
                            <input className="regex-input" value={this.props.regex} onChange={this.props.updateRegex}/></div>
                    </div>
                </div>
            </li>
        );
    }
}

export default SelectorConfig;
