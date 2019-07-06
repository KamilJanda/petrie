import React, {Component} from 'react';
import {Grid, withStyles} from "@material-ui/core";
import TextField from "@material-ui/core/TextField";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Button from "@material-ui/core/Button";
import CardActions from "@material-ui/core/CardActions";
import Icon from "@material-ui/core/Icon";
import DeleteIcon from '@material-ui/icons/Delete';
import ButtonGroup from "@material-ui/core/ButtonGroup";


const styles = theme => ({
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    textField: {
        marginLeft: theme.spacing(1),
        marginRight: theme.spacing(1),
    },
    dense: {
        marginTop: theme.spacing(2),
    },
    menu: {
        width: 200,
    },
    card: {
        minWidth: 275,
    },
    button: {
        margin: theme.spacing(1),
    },
    response: {
        display: 'flex',
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "column",
    },
    rightIcon: {
        marginLeft: theme.spacing(1),
    },
    styleMargin: {
        margin: theme.spacing(1),
    }
});

const wsUri = "ws://localhost:9000/core/links/async";

class StreamCrawler extends Component {

    constructor(props) {
        super(props);

        this.state = {
            isConnected: false,
            request: null,
            response: []
        };

        this.socket = new WebSocket(wsUri);
    }

    componentDidMount() {
        this.initSocket();
    }

    initSocket = () => {
        const self = this;

        this.socket.addEventListener('open', function (event) {
            self.setState({
                isConnected: true
            });
            console.log("Connection established");
        });

        this.socket.addEventListener('message', function (event) {
            console.log('Message from server ', event.data);
            self.setState(prevState => ({
                response: [...prevState.response, event.data]
            }))
        });

        this.socket.addEventListener('close', function (event) {
            self.setState({
                isConnected: false
            });
            console.log("Connection closed");
        });
    };

    componentWillUnmount() {
        if (this.state.isConnected)
            this.socket.close()
    }

    disconnect = () => {
        if (this.state.isConnected)
            this.socket.close();

    };

    connect = () => {
        if (!this.state.isConnected) {
            this.socket = new WebSocket(wsUri);
            this.initSocket();
        }
    };

    sendRequest = () => {
        const request = this.state.request;

        if (request !== null)
            this.socket.send(request);
    };

    handleChange = event => {
        this.setState({
            request: event.target.value
        })
    };

    render() {
        const {classes} = this.props;

        return (
            <>
                <Grid
                    container
                    direction="row"
                    justify="center"
                    alignItems="center"
                >
                    <Grid item xs={12}>
                        <Card className={classes.card}>
                            <CardContent>
                                <TextField
                                    id="request"
                                    label="Request message"
                                    fullWidth
                                    defaultValue='{"rootUrl": "", "depth": 0}'
                                    className={classes.textField}
                                    margin="normal"
                                    onChange={this.handleChange}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}

                                />
                                <div>
                                    <TextField
                                        id="response"
                                        label="Response"
                                        multiline
                                        rows="6"
                                        fullWidth
                                        value={this.state.response}
                                        className={classes.textField}
                                        margin="normal"
                                        variant="outlined"
                                        InputProps={{
                                            readOnly: true,
                                        }}
                                    />
                                </div>
                                <h4 className={classes.styleMargin}>Connected: {this.state.isConnected.toString()}</h4>
                            </CardContent>
                            <CardActions disableSpacing className={classes.styleMargin}>

                                <Button
                                    variant="contained"
                                    color="primary"
                                    className={classes.button}
                                    onClick={this.sendRequest}
                                >
                                    Send
                                    <Icon className={classes.rightIcon}>send</Icon>
                                </Button>
                                <ButtonGroup variant="contained">
                                    <Button
                                        onClick={this.connect}
                                    >
                                        Connect
                                        <Icon className={classes.rightIcon}>send</Icon>
                                    </Button>
                                    <Button
                                        onClick={this.disconnect}
                                    >
                                        Disconnect
                                        <DeleteIcon className={classes.rightIcon}/>
                                    </Button>
                                </ButtonGroup>
                            </CardActions>
                        </Card>
                    </Grid>
                </Grid>
            </>
        );
    }
}

export default withStyles(styles)(StreamCrawler)
