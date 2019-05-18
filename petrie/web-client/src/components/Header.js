import React from 'react';


function Header() {
    return (
        <div style={headerStyle}>
            <h1>Crawler test</h1>
        </div>
    );
}

const headerStyle = {
    background: '#333',
    color: '#fff',
    textAlign: 'center',
    padding: '10px'
};

export default Header;