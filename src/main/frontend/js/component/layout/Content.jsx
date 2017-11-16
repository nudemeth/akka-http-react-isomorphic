import React from 'react';
import {Route, Link} from 'react-router-dom'
import Routes from '../route/Routes.jsx';

class Content extends React.Component {
    constructor(props) {
        super(props);
    }

    renderComponent = () => {
        return Routes.map((route, index) => (
            <Route key={index} path={route.path} exact={route.exact} render={() => (<div>{route.component(this.props.model)}</div>)} />
        ));
    }

    render() {
        return (
            <main role="main">
                {this.renderComponent()}
            </main>
        );
    }
}

module.exports = Content;