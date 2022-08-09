<template>
    <el-cascader :value="cascaderValue" filterable :props="{ expandTrigger: 'hover' }" v-bind="$attrs"
        :options="options" @change="change" :key="key" @visible-change="changeKey"></el-cascader>
</template>
<script>
export default {
    props: {
        value: {
            type: String | Array
        },
        options: {
            type: Array,
            default: () => []
        },
        isString: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            key: 1
        }
    },
    methods: {
        change(e) {
            let value
            if (this.isString) {
                value = e.join('.')
            } else {
                value = e
            }
            // console.log(value)
            this.$emit('input', value)
            this.$emit('change', value)

        },
        changeKey(is) {
            if (is) {
                return
            }

            setTimeout(() => {
                this.key++
            }, 200)

        },
    },
    computed: {
        cascaderValue() {
            if (this.isString) {
                return this.value ? this.value.split('.') : []
            } else {
                return this.value
            }
        }
    },
    watch: {
        options() {
            this.key++
        }
    }





}


</script>