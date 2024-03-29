const styles = theme => ({
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    textField: {
        marginLeft: theme.spacing(1),
        marginRight: theme.spacing(1),
        width: '98%',
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
    buttonGroup: {
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
    },
    mainGird: {
        marginTop: "70px",
    },
    group: {
        margin: theme.spacing(1),
    },
    checkbox: {
        margin: theme.spacing(1),
        marginTop: theme.spacing(4)
    },
    notVisible: {
        display: "none"
    },
});

export default styles
