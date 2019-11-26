import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import DeleteIcon from '@material-ui/icons/Delete';
import CardContent from "@material-ui/core/CardContent";
import TextField from "@material-ui/core/TextField";
import MenuItem from "@material-ui/core/MenuItem";

const styles = theme => ({
    root: {
        color: theme.palette.text.primary,
    },
    icon: {
        margin: theme.spacing(1),
        fontSize: 32,
        position: "absolute",
        top: "0",
        right: "0",
    },
    urlPriority: {
        marginTop: "20px",
        marginBottom: "10px",
        backgroundColor: "#fff",
        borderRadius: "10px",
        boxShadow: "0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22)",
        position: "relative",
        overflow: "hidden",
        width: "100%",
        maxWidth: "100%",
        minHeight: "50px",
    },
    urlPriorityContent: {
        marginLeft: "30px",
        marginRight: "30px",
    },
    buttonGroup: {
        margin: theme.spacing(3)
    },
    checkbox: {
        margin: theme.spacing(1),
        marginTop: theme.spacing(3),
    },
    combobox: {
        margin: theme.spacing(1),
        marginTop: theme.spacing(3),
    },
    notVisible: {
        display: "none"
    },
    group: {
        margin: theme.spacing(1),
    },
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    textField: {
        marginLeft: theme.spacing(1),
        marginRight: theme.spacing(1),
        width: 300,
    },
    menu: {
        width: 200,
    },
    noStyle: {
        listStyleType: "none",
    },
});

const priorities = [
    {
        value: 'HighPriority',
        label: 'High Priority',
    },
    {
        value: 'StandardPriority',
        label: 'Standard Priority',
    },
    {
        value: 'LowPriority',
        label: 'Low Priority',
    }
];

class UrlPriority extends Component {

    constructor(props) {
        super(props);

        this.state = {
            url: "",
            urlPriority: this.props.defaultPriority,
        }
    }

    handleUrlChange = event => {
        this.setState({url: event.target.value});
        this.props.onChange(this.props.id, {url: this.state.url, priority: this.state.urlPriority})
    };

    handlePriorityChange = event => {
        this.setState({urlPriority: event.target.value});
        this.props.onChange(this.props.id, {url: this.state.url, priority: this.state.urlPriority})
    };

    render() {
        const {classes} = this.props;

        return (
            <li key={this.props.id} className={classes.noStyle}>
                <div className={classes.urlPriority}>

                    <DeleteIcon data-id={this.props.id} className={classes.icon}
                                onClick={() => this.props.close(this.props.id)}
                    />

                    <CardContent>
                        <TextField
                            required
                            id="standard-required"
                            label="Url"
                            defaultValue=""
                            className={classes.textField}
                            onChange={this.handleUrlChange}
                            margin="normal"
                        />
                        <TextField
                            id="standard-select-currency"
                            select
                            label="Select searching priority for url"
                            className={classes.textField}
                            value={this.state.urlPriority}
                            onChange={this.handlePriorityChange}
                            SelectProps={{
                                MenuProps: {
                                    className: classes.menu,
                                },
                            }}
                            margin="normal"
                        >
                            {priorities.map(option => (
                                <MenuItem key={option.value} value={option.value}>
                                    {option.label}
                                </MenuItem>
                            ))}
                        </TextField>


                    </CardContent>
                </div>
            </li>
        )
    }
}

export default withStyles(styles)(UrlPriority)
