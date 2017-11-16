import React from 'react';

class Home extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            model: this.props.model || {greeting: ''}
        }
    }

    loadModelFromServer = () => {
        let url = '/data/home';
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
            console.error(`Cannot fetch data from the server: url=${url}, error=${error.message}`)
        });
    }

    componentDidMount = () => {
        this.loadModelFromServer();
    }

    render() {
        return (
            <h2>{this.state.model.greeting}</h2>
        );
    }
}

module.exports = Home;