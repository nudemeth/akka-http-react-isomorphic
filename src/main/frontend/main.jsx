import React from 'react'
import ReactDOM from 'react-dom'
import ReactDOMServer from 'react-dom/server'
import Container from './js/component/layout/Container.jsx'

class Frontend {
    renderServer = (data) => {
        return ReactDOMServer.renderToString(
            <Container data={data} />
        );
    }

    renderClient = (data) => {
        return ReactDOM.render(
            <Container data={data} />,
            document.getElementById('body')
        );
    }
}

exports.Frontend = Frontend;