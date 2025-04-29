import { Ionicons } from '@expo/vector-icons'
import clsx from 'clsx'
import { useRouter } from 'expo-router'
import { Pressable } from 'react-native'

interface BackSignProps {
    color?: string
    size?: number
    bgColor?: string
    shadow?: boolean
}

const BackSign = ({
    color = 'white',
    size = 28,
    bgColor = '#80808095',
    shadow = true,
}: BackSignProps) => {
    const router = useRouter()

    return (
        <Pressable
            onPress={() => router.back()}
            className={clsx(
                'rounded-full p-3',
                shadow && 'shadow-lg shadow-black/50 active:opacity-80'
            )}
            style={{ backgroundColor: bgColor }}
        >
            <Ionicons name="chevron-back" size={size} color={color} />
        </Pressable>
    )
}

export default BackSign
