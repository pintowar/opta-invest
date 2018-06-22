<template>
  <div>
    <img src="@/assets/opta-logo.png">
    <div class="help">
      <h1>{{ msg }}</h1>
      <h2>Essential Links</h2>
      <ul>
        <li><a href="https://www.optaplanner.org" target="_blank">Project Home</a></li>
        <li><a href="https://docs.optaplanner.org/7.7.0.Final/optaplanner-docs/html_single/index.html" target="_blank">Core Docs</a></li>
        <li><a href="https://twitter.com/optaplanner" target="_blank">Twitter</a></li>        
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'optaplanner',
  data () {
    return {
      msg: 'This App uses Optaplanner'
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
