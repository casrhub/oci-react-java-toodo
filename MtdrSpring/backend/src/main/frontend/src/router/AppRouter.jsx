import React from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import LandingPage from '../pages/LandingPage';
import Home from '../pages/Home';
import LoginPage from '../pages/LoginPage'; 

function AppRouter() {
  return (
    <Router>
      <Switch>
        {/* Landing/welcome page */}
        <Route exact path="/" component={LandingPage} />

        {/* Login page */}
        <Route path="/login" component={LoginPage} />

        {/* Existing home (todo list) page */}
        <Route path="/home" component={Home} />
      </Switch>
    </Router>
  );
}

export default AppRouter;
