import React from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';

// Pages
import LandingPage from '../pages/LandingPage';
import DevLoginPage from '../pages/DevLoginPage';
import ManagerLoginPage from '../pages/ManagerLoginPage';
import DevMainMenuPage from '../pages/DevMainMenuPage';
import ManagerMainMenuPage from '../pages/ManagerMainMenuPage';
import Home from '../pages/Home';

function AppRouter() {
  return (
    <Router>
      <Switch>
        {/* Landing */}
        <Route exact path="/" component={LandingPage} />

        {/* Role-specific Login */}
        <Route path="/dev-login" component={DevLoginPage} />
        <Route path="/manager-login" component={ManagerLoginPage} />

        {/* Role-specific Main Menus */}
        <Route path="/dev-main" component={DevMainMenuPage} />
        <Route path="/manager-main" component={ManagerMainMenuPage} />

        {/* Tasks (shared or dev) */}
        <Route path="/home" component={Home} />
      </Switch>
    </Router>
  );
}

export default AppRouter;
