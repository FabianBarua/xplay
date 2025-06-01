

import './App.css'
import reactLynxLogo from './assets/react-logo.png'

export function App() {

  return (
    <view className='App Background'>
      <image src={reactLynxLogo} className='Logo--react' />
      <text className="title">
        Welcome to Lynx React!
      </text>
    </view>
  )
}
