import Vue from 'vue'
import Router from 'vue-router'
import Vuejs from '@/pages/help/vuejs'
import Optaplanner from '@/pages/help/optaplanner'
import Boot from '@/pages/help/boot'
import Portfolios from '@/pages/portfolio/index'
import Portfolio from '@/pages/portfolio/show'

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
      path: '/vuejs',
      name: 'vuejs',
      component: Vuejs
    },
    {
      path: '/optaplanner',
      name: 'optaplanner',
      component: Optaplanner
    },
    {
      path: '/boot',
      name: 'boot',
      component: Boot
    }
  ]
})
