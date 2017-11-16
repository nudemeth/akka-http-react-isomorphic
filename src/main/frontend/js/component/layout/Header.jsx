import React from 'react';
import {Link} from 'react-router-dom';
import {Router} from 'react-router';

class Header extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <header>
                <h1>This is header</h1>
                <ul>
                    <li><Link to="/">Home</Link></li>
                    <li><Link to="/about">About</Link></li>
                </ul>
            </header>
        );
    }
}

module.exports = Header;