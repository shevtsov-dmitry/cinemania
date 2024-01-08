import React from "react";
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import App from '../App';
import SecondPage from './SecondPage';
import {VideoMaterial} from "./video_material/VideoMaterial";

const Routes = () => {
    return (
        <Router>
            <Switch>
                <Route path="/" exact component={App} />
                <Route path="/video-material" exact component={VideoMaterial} />
            </Switch>
        </Router>
    )
}