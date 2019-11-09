import React from "react";
import {makeStyles} from "@material-ui/core";
import DeleteIcon from '@material-ui/icons/Delete';
import TextField from "@material-ui/core/TextField";


const useStyles = makeStyles(theme => ({
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
    noStyle: {
        listStyleType: "none",
    },
    config: {
        marginTop: "40px",
        backgroundColor: "#fff",
        borderRadius: "10px",
        position: "relative",
        overflow: "hidden",
        width: "100%",
        maxWidth: "100%",
        minHeight: "150px",
        boxShadow: "0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22)",
    },
    configContent: {
        marginLeft: "30px",
        marginRight: "30px",
    },
    configTitle: {
        margin: theme.spacing(1),
    },
    regexInputContainer: {
        margin: theme.spacing(1),
    },
}));

export default function FetchDataFromConfig(props) {
    const classes = useStyles();

    let config = {"selector": "", "name": ""};

    return (
        <li key={props.id} className={classes.noStyle}>
            <div className={classes.config}>
                <DeleteIcon data-id={props.id} className={classes.icon} onClick={() => props.close(props.id)}/>
                <div className={classes.configContent}>
                    <h2 className={classes.configTitle}>{props.configTitle}</h2>
                    <div className={classes.regexInputContainer}>
                        <TextField
                            id="name"
                            label={"name"}
                            fullWidth
                            margin="normal"
                            onChange={(event) => {
                                config = {...config, name: event.target.value}
                                props.update(props.id, config)
                            }}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                        <TextField
                            id="fieldSelector"
                            label={"selector"}
                            fullWidth
                            margin="normal"
                            onChange={(event) => {
                                config = {...config, selector: event.target.value}
                                props.update(props.id, config)
                            }}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                    </div>
                </div>
            </div>
        </li>
    );

}
