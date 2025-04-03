import { create } from 'zustand'

interface AdminPanelState {
    isAdminPanelVisible: boolean
    showAdminPanel: () => void
    hideAdminPanel: () => void
    toggleAdminPanel: () => void
}

const useAdminPanelState = create<AdminPanelState>((set) => ({
    isAdminPanelVisible: false,
    showAdminPanel: () => set(() => ({ isAdminPanelVisible: true })),
    hideAdminPanel: () => set(() => ({ isAdminPanelVisible: false })),
    toggleAdminPanel: () =>
        set((state) => ({
            isAdminPanelVisible: !state.isAdminPanelVisible,
        })),
}))

export default useAdminPanelState
