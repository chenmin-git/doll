<template>
  <div>{{ allOrders.filter(o => o.status === 1).length }}</div>
</template>
