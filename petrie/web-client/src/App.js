import React, {Component} from 'react';
import './App.css';
import SimpleCrawler from "./components/Crawling/SimpleCrawler";
import {BrowserRouter as Router, Route} from "react-router-dom";
import StreamCrawler from "./components/Crawling/StreamCrawler";
import Layout from "./components/Layout/Layout";


class App extends Component {
    render() {
        return (
            <Router>
                <Layout>
                    <Route exact path="/crawler" component={SimpleCrawler}/>
                    <Route path="/crawler/simple" component={SimpleCrawler}/>
                    <Route path="/crawler/stream" component={StreamCrawler}/>
                </Layout>
            </Router>
        );
    }
}

export default App;
