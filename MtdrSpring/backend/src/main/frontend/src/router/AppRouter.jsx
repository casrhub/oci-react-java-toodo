import React from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import LandingPage from '../pages/LandingPage';
import Home from '../pages/Home';
import LoginPage from '../pages/LoginPage';
import MainMenuPage from '../pages/MainMenuPage'; 

function AppRouter() {
  return (
    <Router>
      <Switch>
        <Route exact path="/" component={LandingPage} />
        <Route path="/login" component={LoginPage} />
        <Route path="/main" component={MainMenuPage} /> 
        <Route path="/home" component={Home} />
      </Switch>
    </Router>
  );
}

export default AppRouter;
