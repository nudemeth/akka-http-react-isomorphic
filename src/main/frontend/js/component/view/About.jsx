import React from 'react';
import TitleBar from '../browser/TitleBar.jsx';

class About extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            model: this.props.model || {text: ''}
        }
    }

    loadModelFromServer = () => {
        let url = '/data/about';
        let header = new Headers({"Content-type": "application/json"});
        let init = {
            method: 'GET',
            header: header,
            cache: 'no-cache'
        };
        let request = new Request(url, init);
        fetch(request).then((response) => {
            if (response.ok) {
                return response.json();
            }
            throw new Error(`Network response was not ok: status=${response.status}`);
        }).then((result) => {
            this.setState({model: result});
        }).catch((error) => {
            console.error(`Cannot fetch data from server: url=${url}, error=${error.message}`)
        });
    }

    componentDidMount() {
        this.loadModelFromServer();
    }

    render() {
        return (
            <div>
                <TitleBar title={this.state.model.title} />
                <h2>{this.state.model.text}</h2>
            </div>
        );
    }
}

module.exports = About;