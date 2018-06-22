<template>
  <div>
    <b-navbar toggleable="md" type="dark" variant="primary">
      <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>
      <b-navbar-brand to="/">Opta Invest</b-navbar-brand>
      <b-collapse is-nav id="nav_collapse">
        <b-navbar-nav>
          <b-nav-item to="/">Portfolios</b-nav-item>
          <b-nav-item-dropdown text="Help">
            <b-dropdown-item to="/optaplanner">Optaplanner</b-dropdown-item>
            <b-dropdown-item to="/vuejs">Vue.js</b-dropdown-item>
            <b-dropdown-item to="/boot">Spring Boot</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <div id="app">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: 'app'
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
