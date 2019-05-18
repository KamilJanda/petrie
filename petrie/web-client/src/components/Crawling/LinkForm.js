import React from "react";
import './LinkForm.css';


class LinkForm extends React.Component {
    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.state = {
            response: []
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
            })
        }).then(res => res.json())
            .then(response =>
                // console.log('Success:', JSON.stringify(response)))
                this.setState({response: JSON.stringify(response)})
            )
            .catch(error => {
                console.error('Error:', error);
                this.setState({response: error.message})
            });
    }

    render() {
        return (
            <div className="container">
                <div className="request-container">
                    <form style={styles} onSubmit={this.handleSubmit}>

                        <h1 style={headerStyle}>Crawler test</h1>

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
            </div>
        );
    }
}

const styles = {
    // backgroundColor: '#888',
    // padding: '20px',
    // border: 'solid 1px #222'
};

const headerStyle = {
    padding: '20px',
};


export default LinkForm;