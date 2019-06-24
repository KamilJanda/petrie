import React, {Component} from 'react';
import './App.css';
import LinkForm from "./components/Crawling/LinkForm";
import {BrowserRouter as Router, Route} from "react-router-dom";
import AsyncCrawler from "./components/Crawling/AsyncCrawler";
import Layout from "./components/Layout/Layout";


class App extends Component {
    render() {
        return (
            <Router>
                {/*<div className="App">*/}
                {/*<div className="content-wrapper">*/}
                <Layout>
                    {/*<Route path="/crawler" component={LinkForm}/>*/}
                    {/*<main>*/}
                    {/*<Route exact path="/" component={Home} />*/}
                    <Route exact path="/crawler" component={LinkForm}/>
                    <Route path="/crawler/base" component={LinkForm}/>
                    <Route path="/crawler/async" component={AsyncCrawler}/>
                    {/*</main>*/}
                </Layout>
                {/*</div>*/}
                {/*</div>*/}
            </Router>
        );
    }
}

export default App;
