<template>
  <div>
    <h1>{{ msg }}</h1>
    <b-table striped hover :fields="fields" :items="items">
      <template slot="id" slot-scope="row">
        <router-link :to="{ name: 'portfolio', params: { id: row.value }}">{{row.value}}</router-link>
      </template>
      <template slot="actions" slot-scope="row">
        <b-button size="sm">
          Details
        </b-button>
      </template>
    </b-table>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'portfolios',
  data () {
    return {
      msg: 'Portfolio List',
      items: [],
      fields: [
        { key: 'id', label: 'Portfolio ID', sortable: true },
        { key: 'name', label: 'Portfolio Name', sortable: true, 'class': 'text-center' },
        { key: 'actions', label: 'Actions' }
      ]
    }
  },
  mounted () {
    axios.get('/api/portfolios').then(res => {
      this.items = res.data.map(e => ({id: e.id, name: e.name}))
    }, error => {
      console.error(error)
    })
  }
}
</script>