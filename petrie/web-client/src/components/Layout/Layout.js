import React, {Component} from 'react';
import MenuList from "@material-ui/core/MenuList";
import MenuItem from "@material-ui/core/MenuItem";
import {Link, withRouter} from "react-router-dom";
import Divider from "@material-ui/core/Divider";
import CssBaseline from "@material-ui/core/CssBaseline";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import Hidden from "@material-ui/core/Hidden";
import Drawer from "@material-ui/core/Drawer";
import {withStyles} from '@material-ui/core/styles'
import Collapse from "@material-ui/core/Collapse";
import MenuIcon from '@material-ui/icons/Menu';
import {compose} from 'recompose'
import Grid from "@material-ui/core/Grid";

const drawerWidth = 230;

const styles = theme => ({
    root: {
        display: 'flex',
    },
    drawer: {
        [theme.breakpoints.up('sm')]: {
            width: drawerWidth,
            flexShrink: 0,
        },
    },
    appBar: {
        marginLeft: drawerWidth,
        backgroundColor: "#fff",
        color: "#6c7293",
        [theme.breakpoints.up('sm')]: {
            width: `calc(100% - ${drawerWidth}px)`,
        },
    },
    menuButton: {
        marginRight: theme.spacing(2),
        [theme.breakpoints.up('sm')]: {
            display: 'none',
        },
    },
    navigation: {
        ...theme.mixins.toolbar,
        ...{backgroundColor: "#232939"}

    },
    toolbar: theme.mixins.toolbar,
    drawerPaper: {
        backgroundColor: "#232939",
        width: drawerWidth,
        color: "#606a8c",
    },
    content: {
        flexGrow: 1,
        padding: theme.spacing(3),
    },
    nested: {
        paddingLeft: theme.spacing(4),
    },

});


class Layout extends Component {
    state = {
        mobileOpen: false,
        crawlingOpen: false
    };

    handleDrawerToggle = () => {
        this.setState({mobileOpen: !this.state.mobileOpen})
    };

    handleCrawlingClick = () => {
        this.setState({crawlingOpen: !this.state.crawlingOpen})
    };

    render() {
        const {container} = this.props;
        const {classes, location: {pathname}} = this.props;
        const {mobileOpen} = this.state;


        const drawer = (
            <>
                <div className={classes.navigation}/>
                <Divider/>
                <MenuList>
                    <MenuItem component={Link} to="/" selected={pathname === "/"}>
                        Home
                    </MenuItem>

                    <MenuItem
                        component={Link}
                        to="/crawler"
                        selected={pathname === "/crawler"}
                        button
                        onClick={this.handleCrawlingClick}
                    >
                        Crawling
                    </MenuItem>
                    <Collapse in={this.state.crawlingOpen} timeout="auto" unmountOnExit>
                        <MenuList>
                            <MenuItem
                                className={classes.nested}
                                component={Link}
                                to="/crawler/base"
                                selected={pathname === "/crawler/base"}
                            >
                                Simple Crawling
                            </MenuItem>
                            <MenuItem
                                className={classes.nested}
                                component={Link}
                                to="/crawler/async"
                                selected={pathname === "/crawler/async"}
                            >
                                Asynchronous Crawling
                            </MenuItem>
                        </MenuList>
                    </Collapse>
                </MenuList>
            </>
        );

        return (
            <div className={classes.root}>
                <CssBaseline/>
                <AppBar position="fixed" className={classes.appBar}>
                    <Toolbar>
                        <IconButton
                            color="inherit"
                            aria-label="Open drawer"
                            edge="start"
                            onClick={this.handleDrawerToggle}
                            className={classes.menuButton}
                        >
                            <MenuIcon/>
                        </IconButton>
                        <Typography variant="h6" noWrap>
                            Petrie
                        </Typography>
                    </Toolbar>
                </AppBar>
                <nav className={classes.drawer} aria-label="Mailbox folders">
                    <Hidden smUp implementation="css">
                        <Drawer
                            container={container}
                            variant="temporary"
                            open={mobileOpen}
                            onClose={this.handleDrawerToggle}
                            classes={{
                                paper: classes.drawerPaper,
                            }}
                            ModalProps={{
                                keepMounted: true, // Better open performance on mobile.
                            }}
                        >
                            {drawer}
                        </Drawer>
                    </Hidden>
                    <Hidden xsDown implementation="css">
                        <Drawer
                            classes={{
                                paper: classes.drawerPaper,
                            }}
                            variant="permanent"
                            open
                        >
                            {drawer}
                        </Drawer>
                    </Hidden>
                </nav>
                <main className={classes.content}>
                    <div className={classes.toolbar}/>
                    <Grid container>
                        {this.props.children}
                    </Grid>
                </main>
            </div>
        );

    }
}


export default compose(
    withStyles(styles),
    withRouter
)(Layout);
