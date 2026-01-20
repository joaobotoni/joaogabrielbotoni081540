export interface Toast {
    message: string,
    type?: string,
    backColor: string,
    textColor: string,
    iconColor: string,
    iconPath: string
}

export const Error = (message: string): Toast => ({
    message: message,
    type: 'error',
    backColor: 'bg-red-50',
    textColor: 'text-red-800',
    iconColor: "text-red-400",
    iconPath: pathError
})

export const Success = (message: string): Toast => ({
    message: message,
    type: 'success',
    backColor: 'bg-green-50',
    textColor: 'text-green-800',
    iconColor: 'text-green-400',
    iconPath: pathSuccess
})

export const Warning = (message: string): Toast => ({
    message: message,
    backColor: 'bg-amber-50',
    textColor: 'text-amber-800',
    iconColor: 'text-amber-400',
    iconPath: pathWarning
})

const pathSuccess: string = `M10 18a8 8 0 100-16 8 8 0 000 16zm3.857-9.809a.75.75 
0 00-1.214-.882l-3.483 4.79-1.88-1.88a.75.75 0 10-1.06 1.061l2.5 2.5a.75.75 0 
001.137-.089l4-5.5z`

const pathWarning: string = `M8.485 3.495c.673-1.167 2.357-1.167 3.03 0l6.28 10.875c.673 
1.167-.17 2.625-1.516 2.625H3.72c-1.347 0-2.189-1.458-1.515-2.625l6.28-10.875zM10 6a.75.75
 0 01.75.75v3.5a.75.75 0 01-1.5 0v-3.5A.75.75 0 0110 6zm0 9a1 1 0 100-2 1 1 0 000 2z`

const pathError: string = `M10 18a8 8 0 100-16 8 8 0 000 16zM8.28 7.22a.75.75 0 
00-1.06 1.06L8.94 10l-1.72 1.72a.75.75 0 101.06 1.06L10 11.06l1.72 1.72a.75.75 0 
101.06-1.06L11.06 10l1.72-1.72a.75.75 0 00-1.06-1.06L10 8.94 8.28 7.22z`



