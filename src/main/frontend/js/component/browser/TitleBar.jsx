import React from 'react';

class TitleBar extends React.Component {
    constructor(props) {
        super(props);
    }

    componentWillMount = () => {
        if (!!window.document) {
            document.title = this.props.title;
        }
    }

    componentWillUpdate = (nextProps, nextState) => {
        if (!!window.document) {
            document.title = nextProps.title;
        }
    }

    render() {
        return null;
    }
}

module.exports = TitleBar;