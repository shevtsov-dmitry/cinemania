import { ReactElement } from 'react'
import { Image, Text, View } from 'react-native'

const Footer = (): ReactElement => {
    return (
        <View className="w-full bg-neutral-900 py-5">
            <View className="flex-row flex-wrap items-center justify-around">
                {/* Левая часть — логотип и описание */}
                <View className="max-w-[50%] pr-4">
                    <Image
                        source={require('@/images/icons/company_logo.png')}
                        className="mb-2 w-2"
                    />
                    <Text className="text-s leading-5 text-white">
                        Онлайн-платформа для стриминга видео. Мы предоставляем
                        доступ к фильмам, сериалам и другому видеоконтенту в
                        любое время на любых устройствах.
                    </Text>
                </View>

                {/* Правая часть — контакты */}
                <View className="max-w-[45%] items-end">
                    <View className={'flex flex-row gap-3'}>
                        <Text className="text-xs text-white underline">
                            Адрес офиса:
                        </Text>
                        <Text
                            className="mb-1 text-right text-xs text-white"
                            style={{ fontStyle: 'italic' }}
                        >
                            г. Воронеж, ул. Профессора Попова, д. 23, БЦ
                            «Технопарк»
                        </Text>
                    </View>

                    <View className={'flex flex-row gap-3'}>
                        <Text className="text-xs text-white underline">
                            Телефон:
                        </Text>
                        <Text className="mb-1 text-right text-xs text-white">
                            +7 (812) 123-45-67
                        </Text>
                    </View>

                    <View className={'flex flex-row gap-3'}>
                        <Text className="text-xs text-white underline">
                            Электронная почта:
                        </Text>
                        <Text className="mb-1 text-right text-xs text-white">
                            support@streamera.ru
                        </Text>
                    </View>
                </View>
            </View>

            <View className="mt-4 border-t border-gray-700 pt-2">
                <Text className="text-center text-[11px] text-white">
                    © 2025 StreamEra. Все права защищены.
                </Text>
                <Text className="text-center text-[10px] text-white underline">
                    Политика конфиденциальности | Пользовательское соглашение
                </Text>
            </View>
        </View>
    )
}

export default Footer
