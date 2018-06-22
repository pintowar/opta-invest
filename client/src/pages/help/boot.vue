<template>
  <div>
    <img src="@/assets/boot-logo.png">
    <div class="help">
      <h1>{{ msg }}</h1>
      <h2>Essential Links</h2>
      <ul>
        <li><a href="https://spring.io/projects/spring-boot" target="_blank">Project Home</a></li>
        <li><a href="https://docs.spring.io/spring-boot/docs/2.0.3.RELEASE/reference/htmlsingle/" target="_blank">Core Docs</a></li>
        <li><a href="https://twitter.com/springboot" target="_blank">Twitter</a></li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'boot',
  data () {
    return {
      msg: 'This is a Spring Boot App'
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
