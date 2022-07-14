export const GetdeepObj = (obj,length=true) =>{
	if (typeof obj == 'object' && !Array.isArray(obj)) {
		let arr = []
		for (let key in obj) {
			if (obj.hasOwnProperty(key)) {
				if (Array.isArray(obj[key])) {
					let obj = {
						value: key,
						label: key,
					}
					if(length){
						obj.children=[{
								value: 'length()',
								label: '长度',
								valueType: 1,
							}]
					}else{
						obj.children=[]
					}
					
					
					arr.push(obj)
				} else if (typeof obj[key] == 'object' && obj[key] != null) {
					arr.push({
						value: key,
						label: key,
						children:GetdeepObj(obj[key],length)
					})
				} else {
					arr.push({
						value: key,
						label: key,
						valueType: typeof obj[key] == 'string' ? 2 : 1,
					})
				}

			}
		}
		return arr
	} else if (Array.isArray(obj)) {
		let obj
		if(length){
			obj=[{
					value: 'length()',
					label: '长度',
					valueType: 1,
				}]
		}else{
			obj=[]
		}
		
		return obj
	}
}
