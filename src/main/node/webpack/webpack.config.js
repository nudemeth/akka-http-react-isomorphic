const webpack = require('webpack');
const path = require('path');
const paths = {
    MAIN: path.resolve('src', 'main'),
    NODE_MODULES: path.resolve('src', 'main', 'node', 'node_modules')
}

module.exports = {
    entry: path.join(paths.MAIN, 'frontend', 'main.jsx'),
    output: {
        path: path.join(paths.MAIN, 'webapp', 'js'),
        filename: 'bundle.js',
        library: ['com', 'nudemeth', 'example', 'web']
    },
    module: {
        loaders: [{
            test: /\.jsx$/,
            loader: 'babel-loader',
            exclude: /node_modules/,
            query: {
                presets: [
                    path.join(paths.NODE_MODULES, 'babel-preset-env'),
                    path.join(paths.NODE_MODULES, 'babel-preset-stage-2'),
                    path.join(paths.NODE_MODULES, 'babel-preset-react'),
                    path.join(paths.NODE_MODULES, 'babel-polyfill')
                ]
            }
        }]
    },
    plugins: [
            new webpack.DefinePlugin({
                'process.env.NODE_ENV': JSON.stringify('production')
            }),
            new webpack.optimize.UglifyJsPlugin({
                sourceMap: false
            })
        ],
    resolve: {
        modules: [paths.NODE_MODULES]
    },
    resolveLoader: {
        modules: [paths.NODE_MODULES]
    }
}