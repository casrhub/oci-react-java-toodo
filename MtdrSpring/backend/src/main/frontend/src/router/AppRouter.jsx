import React from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import LandingPage from '../pages/LandingPage';
import Home from '../pages/Home'; // your existing todo page

function AppRouter() {
  return (
    <Router>
      <Switch>
        {/* Landing/welcome page is now our default path */}
        <Route exact path="/" component={LandingPage} />

        {/* Existing home (todo) page at /home */}
        <Route path="/home" component={Home} />

        {/* Add more routes as needed */}
      </Switch>
    </Router>
  );
}

export default AppRouter;
