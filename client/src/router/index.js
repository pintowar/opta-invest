import Vue from 'vue'
import Router from 'vue-router'
import Help from '@/components/Help'
import Portfolios from '@/components/Portfolios'
import Portfolio from '@/components/Portfolio'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'portfolios',
      component: Portfolios
    },
    {
      path: '/portfolio/:id',
      name: 'portfolio',
      component: Portfolio
    },
    {
      path: '/help',
      name: 'help',
      component: Help
    }
  ]
})
