/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  ListView,
  View,
  Image,
  Button,
  MPush,
  TextInput,
  DeviceEventEmitter,
} from 'react-native';

var {NativeModules}=require('react-native');
var mPush = NativeModules.MPush;

export default class AwesomeProject extends Component {
   constructor(props, context) {
    	super(props, context);
	    this.state = {
	      deviceIdBtnTitle: 'GET DEVICE_ID',
	      accountToBind: 'ACCOUNT TO BIND',
	      tagToAdd: 'TAG TO ADD/DELETE',
	      loaded: false,
	      initInfo: 'Loading...',
	    };
	     this.onDeviceIdClicked = this.onDeviceIdClicked.bind(this);
	     this.onAccountBindClicked = this.onAccountBindClicked.bind(this);
	     this.onAccountUnbindClicked = this.onAccountUnbindClicked.bind(this);
	     this.onAddTagClicked = this.onAddTagClicked.bind(this);
	     this.onDeleteTagClicked = this.onDeleteTagClicked.bind(this);
	  }
  render() {
		return (
			<View >
				<View style={styles.tabbar}>
					<Image source={require('./assets/imgs/logo.png')} style={styles.logo}/>
					<Text style={styles.tabname}>MPush ReactNative Demo</Text>
				</View>
				<View style={styles.btnWrapper}>
	 			<Button 
					onPress = {this.onDeviceIdClicked}
					title = {this.state.deviceIdBtnTitle}
					color = "#841584"
			/>
		</View>

		<View style={styles.btnWrapper}>
			<TextInput 
				style={{height: 40, borderColor: 'gray', borderWidth: 1}}
				onChangeText={(text) => this.setState({accountToBind: text})}
				value={this.state.accountToBind}
			/>
		</View>
		<View style={{flexDirection:'row', margin:15, justifyContent:'space-around'}}>
			<View style={{flex:1, margin:5}}>
		 			<Button 
		 				flex-grow = "1"
		 				title="Consent to privacy"
						color = "#6495ed"
						onPress = {this.InitClicked}
				/>
			</View>
			<View style={{flex:1, margin:5}}>
		 			<Button
		 				flex-grow = "1"
		 				title="BIND ACCOUNT"
						color = "#6495ed"
						onPress = {this.onAccountBindClicked}
				/>
			</View>
			<View style={{flex:1, margin:5}}>
		 			<Button 
		 				flex-grow = "1"
						title = "UNBIND ACCOUNT"
						color = "#6495ed"
						onPress = {this.onAccountUnbindClicked}
				/>
			</View>
		</View>

		<View style={styles.btnWrapper}>
			<TextInput 
				style={{height: 40, borderColor: 'gray', borderWidth: 1}}
				onChangeText={(text) => this.setState({tagToAdd: text})}
				value={this.state.tagToAdd}
			/>
		</View>
		<View style={{flexDirection:'row', margin:15, justifyContent:'space-around'}}>
			<View style={{flex:1, margin:5}}>
		 			<Button 
		 				flex-grow = "1"
		 				title="ADD TAG"
						color = "#6495ed"
						onPress = {this.onAddTagClicked}
				/>
			</View>
			<View style={{flex:1, margin:5}}>
		 			<Button 
		 				flex-grow = "1"
						title = "DELETE TAG"
						color = "#6495ed"
						onPress = {this.onDeleteTagClicked}
				/>
			</View>
		</View>
			</View>
			);
  }
  onDeviceIdClicked() {
  		var that = this;
  		mPush.getDeviceId(function(args) {
  			that.setState({
  				deviceIdBtnTitle: args
  			});
        });
  }
  InitClicked() {
  	mPush.pushInit();
  }
  onAccountBindClicked() {
  	mPush.bindAccount(this.state.accountToBind, function(args) {
  		alert(args);
  	});
  }
  onAccountUnbindClicked() {
  	mPush.unbindAccount(function(args){
  		alert(args);
  	});
  }
  onAddTagClicked() {
  	mPush.addTag(this.state.tagToAdd, function(args){
  		alert(args);
  	});
  }
  onDeleteTagClicked() {
  	mPush.deleteTag(this.state.tagToAdd, function(args){
  		alert(args);
  	})
  }
  renderLoadingView() {
  	return (
  		<View style={styles.container}>
  			<Text>{this.state.initInfo}</Text>
  		</View>
  	);
  }
  	componentDidMount() {
  		DeviceEventEmitter.addListener('onMessage', this.onMessage);
  		DeviceEventEmitter.addListener('onNotification', this.onNotification);
  		DeviceEventEmitter.addListener('onNotificationOpened', this.onNotificationOpened);
  		DeviceEventEmitter.addListener('onNotificationRemoved', this.onNotificationRemoved);
  	}

	componentWillUnmount() {
	    DeviceEventEmitter.removeListener('onMessage', this.onMessage);
	    DeviceEventEmitter.removeListener('onNotification', this.onNotification);
  		DeviceEventEmitter.removeListener('onNotificationOpened', this.onNotificationOpened);
  		DeviceEventEmitter.removeListener('onNotificationRemoved', this.onNotificationRemoved);
	}
	onMessage(e){
	    alert("Message Received. Title:" + e.title + ", Content:" + e.content);
	}
	onNotification(e){
		alert("Notification Received.Title:" + e.title + ", Content:" + e.content);
	}
	onNotificationOpened(e) {
		alert("Notification Clicked");
	}
	onNotificationRemoved(e) {
		alert("Notification removed");
	}
}

const styles = StyleSheet.create({
   btnWrapper: {
   	 margin: 15
   },
   tabbar: {
   		flexDirection: 'row',
   		backgroundColor: '#000',
   		padding: 3,
   		justifyContent: 'center',
   		alignItems: 'center'
   },
   logo: {
   		width:30, 
   		height:30,
   },
   tabname: {
   		flex: 1,
   		color: 'white',
   		textAlign: 'center'
   },
  container: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  }
});

AppRegistry.registerComponent('AwesomeProject', () => AwesomeProject);
